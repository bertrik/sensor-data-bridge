package nl.bertriksikken.loraforwarder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import nl.bertriksikken.apeldoorn.ApeldoornMsg;
import nl.bertriksikken.gls.GeoLocationService;
import nl.bertriksikken.luftdaten.LuftdatenConfig;
import nl.bertriksikken.luftdaten.LuftdatenUploader;
import nl.bertriksikken.mydevices.MyDevicesConfig;
import nl.bertriksikken.mydevices.MyDevicesHttpUploader;
import nl.bertriksikken.nbiot.NbIotReceiver;
import nl.bertriksikken.opensense.OpenSenseConfig;
import nl.bertriksikken.opensense.OpenSenseUploader;
import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.PayloadParseException;
import nl.bertriksikken.pm.SensorData;
import nl.bertriksikken.pm.cayenne.TtnCayenneMessage;
import nl.bertriksikken.pm.sps30.Sps30Message;
import nl.bertriksikken.pm.ttnulm.TtnUlmMessage;
import nl.bertriksikken.ttn.MqttListener;
import nl.bertriksikken.ttn.TtnAppConfig;
import nl.bertriksikken.ttn.TtnConfig;
import nl.bertriksikken.ttn.TtnUplinkMessage;
import nl.bertriksikken.ttnv3.enddevice.EndDevice;
import nl.bertriksikken.ttnv3.enddevice.EndDeviceRegistry;

public final class LoraLuftdatenForwarder {

    private static final Logger LOG = LoggerFactory.getLogger(LoraLuftdatenForwarder.class);
    private static final String CONFIG_FILE = "loraluftdatenforwarder.yaml";

    private final NbIotReceiver nbIotReceiver;
    private final List<MqttListener> mqttListeners = new ArrayList<>();
    private final LuftdatenUploader luftdatenUploader;
    private final OpenSenseUploader openSenseUploader;
    private final MyDevicesHttpUploader myDevicesUploader;
    private final GeoLocationService geoLocationService;
    private final Map<String, EndDeviceRegistry> deviceRegistries = new HashMap<>();
    private final Map<String, CommandHandler> commandHandlers = new HashMap<>();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) throws IOException, MqttException {
        PropertyConfigurator.configure("log4j.properties");

        LoraForwarderConfig config = readConfig(new File(CONFIG_FILE));
        LoraLuftdatenForwarder app = new LoraLuftdatenForwarder(config);
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    private LoraLuftdatenForwarder(LoraForwarderConfig config) throws IOException {
        nbIotReceiver = new NbIotReceiver(config.getNbIotConfig());

        LuftdatenConfig luftdatenConfig = config.getLuftdatenConfig();
        luftdatenUploader = LuftdatenUploader.create(luftdatenConfig);

        OpenSenseConfig openSenseConfig = config.getOpenSenseConfig();
        openSenseUploader = OpenSenseUploader.create(openSenseConfig);

        MyDevicesConfig myDevicesConfig = config.getMyDevicesConfig();
        myDevicesUploader = MyDevicesHttpUploader.create(myDevicesConfig);

        geoLocationService = GeoLocationService.create(config.getGeoLocationConfig());

        TtnConfig ttnConfig = config.getTtnConfig();
        for (TtnAppConfig appConfig : config.getTtnConfig().getApps()) {
            // add listener for each app
            EPayloadEncoding encoding = appConfig.getEncoding();
            LOG.info("Adding MQTT listener for TTN application '{}' with encoding '{}'", appConfig.getName(), encoding);
            MqttListener listener = new MqttListener(ttnConfig, appConfig, uplink -> messageReceived(appConfig, uplink));
            mqttListeners.add(listener);

            // for each app, create a device registry client so we can look up attributes
            EndDeviceRegistry deviceRegistry = EndDeviceRegistry.create(ttnConfig.getIdentityServerUrl(),
                    ttnConfig.getIdentityServerTimeout(), appConfig);
            deviceRegistries.put(appConfig.getName(), deviceRegistry);

            // register command handler
            CommandHandler commandHandler = new CommandHandler(geoLocationService, deviceRegistry);
            commandHandlers.put(appConfig.getName(), commandHandler);
        }
    }

    private void messageReceived(TtnAppConfig appConfig, TtnUplinkMessage uplink) {
        LOG.info("Received: '{}'", uplink);

        try {
            // decode and handle command response
            if (uplink.getPort() == CommandHandler.LORAWAN_PORT) {
                CommandHandler commandHandler = commandHandlers.get(uplink.getAppId());
                if (commandHandler != null) {
                    commandHandler.processResponse(uplink);
                }
                return;
            }
            AppDeviceId appDeviceId = new AppDeviceId(uplink.getAppId(), uplink.getDevId());

            // decode and upload telemetry message
            SensorData sensorData = decodeTtnMessage(appConfig.getEncoding(), uplink);
            luftdatenUploader.scheduleUpload(appDeviceId, sensorData);
            openSenseUploader.scheduleUpload(appDeviceId, sensorData);
            myDevicesUploader.scheduleUpload(appDeviceId, sensorData);
        } catch (PayloadParseException e) {
            LOG.warn("Could not parse '{}' payload from: '{}", appConfig.getEncoding(), uplink.getRawPayload());
        }
    }

    // package-private to allow testing
    SensorData decodeTtnMessage(EPayloadEncoding encoding, TtnUplinkMessage uplink) throws PayloadParseException {
        SensorData sensorData = new SensorData();

        // common fields
        if (Double.isFinite(uplink.getRSSI())) {
            sensorData.addValue(ESensorItem.LORA_RSSI, uplink.getRSSI());
        }
        if (Double.isFinite(uplink.getSNR())) {
            sensorData.addValue(ESensorItem.LORA_SNR, uplink.getSNR());
        }
        if (uplink.getSF() > 0) {
            sensorData.addValue(ESensorItem.LORA_SF, (double) uplink.getSF());
        }

        // SPS30 specific decoding
        if (uplink.getPort() == Sps30Message.LORAWAN_PORT) {
            Sps30Message message = Sps30Message.parse(uplink.getRawPayload());
            sensorData.addValue(ESensorItem.PM1_0, message.getPm1_0());
            sensorData.addValue(ESensorItem.PM2_5, message.getPm2_5());
            sensorData.addValue(ESensorItem.PM4_0, message.getPm4_0());
            sensorData.addValue(ESensorItem.PM10, message.getPm10());
            sensorData.addValue(ESensorItem.PM0_5_N, message.getN0_5());
            sensorData.addValue(ESensorItem.PM1_0_N, message.getN1_0());
            sensorData.addValue(ESensorItem.PM2_5_N, message.getN2_5());
            sensorData.addValue(ESensorItem.PM4_0_N, message.getN4_0());
            sensorData.addValue(ESensorItem.PM10_N, message.getN10());
            sensorData.addValue(ESensorItem.PM_TPS, message.getTps());
            return sensorData;
        }

        // specific fields
        switch (encoding) {
        case TTN_ULM:
            TtnUlmMessage ulmMessage = TtnUlmMessage.parse(uplink.getRawPayload());
            sensorData.addValue(ESensorItem.PM10, ulmMessage.getPm10());
            sensorData.addValue(ESensorItem.PM2_5, ulmMessage.getPm2_5());
            sensorData.addValue(ESensorItem.HUMI, ulmMessage.getRhPerc());
            sensorData.addValue(ESensorItem.TEMP, ulmMessage.getTempC());
            break;
        case CAYENNE:
            TtnCayenneMessage cayenne = TtnCayenneMessage.parse(uplink.getRawPayload());
            if (cayenne.hasPm10()) {
                sensorData.addValue(ESensorItem.PM10, cayenne.getPm10());
            }
            if (cayenne.hasPm4()) {
                sensorData.addValue(ESensorItem.PM4_0, cayenne.getPm4());
            }
            if (cayenne.hasPm2_5()) {
                sensorData.addValue(ESensorItem.PM2_5, cayenne.getPm2_5());
            }
            if (cayenne.hasPm1_0()) {
                sensorData.addValue(ESensorItem.PM1_0, cayenne.getPm1_0());
            }
            if (cayenne.hasRhPerc()) {
                sensorData.addValue(ESensorItem.HUMI, cayenne.getRhPerc());
            }
            if (cayenne.hasTempC()) {
                sensorData.addValue(ESensorItem.TEMP, cayenne.getTempC());
            }
            if (cayenne.hasPressureMillibar()) {
                sensorData.addValue(ESensorItem.PRESSURE, 100.0 * cayenne.getPressureMillibar());
            }
            if (cayenne.hasPosition()) {
                double[] position = cayenne.getPosition();
                sensorData.addValue(ESensorItem.POS_LAT, position[0]);
                sensorData.addValue(ESensorItem.POS_LON, position[1]);
                sensorData.addValue(ESensorItem.POS_ALT, position[2]);
            }
            break;
        case APELDOORN:
            ApeldoornMsg apeldoornMsg = ApeldoornMsg.parse(uplink.getDecodedFields());
            apeldoornMsg.getSensorData(sensorData);
            break;
        default:
            throw new IllegalStateException("Unhandled encoding: " + encoding);
        }
        return sensorData;
    }

    /**
     * Starts the application.
     * 
     * @throws MqttException in case of a problem starting MQTT client
     * @throws IOException
     */
    private void start() throws MqttException, IOException {
        LOG.info("Starting LoraLuftdatenForwarder application");

        nbIotReceiver.start();

        // schedule task to fetch opensense ids
        executor.scheduleAtFixedRate(this::updateAttributes, 0, 60, TimeUnit.MINUTES);

        // start opensense uploader
        openSenseUploader.start();

        luftdatenUploader.start();
        for (MqttListener listener : mqttListeners) {
            listener.start();
        }

        LOG.info("Started LoraLuftdatenForwarder application");
    }

    // retrieves application attributes and notifies each interested components
    private void updateAttributes() {
        // fetch all attributes
        Map<AppDeviceId, AttributeMap> attributes = new HashMap<>();
        for (Entry<String, EndDeviceRegistry> entry : deviceRegistries.entrySet()) {
            String applicationId = entry.getKey();
            EndDeviceRegistry registry = entry.getValue();
            LOG.info("Fetching TTNv3 application attributes for '{}'", applicationId);
            try {
                for (EndDevice device : registry.listEndDevices()) {
                    AppDeviceId appDeviceId = new AppDeviceId(applicationId, device.getDeviceId());
                    attributes.put(appDeviceId, new AttributeMap(device.getAttributes()));
                }
            } catch (IOException e) {
                LOG.warn("Error getting opensense map ids for {}", e.getMessage());
            }
        }
        LOG.info("Fetching TTNv3 application attributes done");

        // notify all uploaders
        openSenseUploader.processAttributes(attributes);
        myDevicesUploader.processAttributes(attributes);
        luftdatenUploader.processAttributes(attributes);
    }

    /**
     * Stops the application.
     * 
     * @throws MqttException
     */
    private void stop() {
        LOG.info("Stopping LoraLuftdatenForwarder application");

        executor.shutdownNow();
        mqttListeners.forEach(MqttListener::stop);
        commandHandlers.values().forEach(CommandHandler::stop);
        openSenseUploader.stop();
        luftdatenUploader.stop();
        nbIotReceiver.stop();

        LOG.info("Stopped LoraLuftdatenForwarder application");
    }

    private static LoraForwarderConfig readConfig(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try (FileInputStream fis = new FileInputStream(file)) {
            return mapper.readValue(fis, LoraForwarderConfig.class);
        } catch (IOException e) {
            LOG.warn("Failed to load config {}, writing defaults", file.getAbsoluteFile());
            LoraForwarderConfig config = new LoraForwarderConfig();
            mapper.writeValue(file, config);
            return config;
        }
    }

}
