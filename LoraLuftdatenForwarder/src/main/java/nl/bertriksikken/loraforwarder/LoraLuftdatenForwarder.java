package nl.bertriksikken.loraforwarder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
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

import nl.bertriksikken.loraforwarder.ttnulm.PayloadParseException;
import nl.bertriksikken.loraforwarder.ttnulm.TtnCayenneMessage;
import nl.bertriksikken.loraforwarder.ttnulm.TtnUlmMessage;
import nl.bertriksikken.luftdaten.ILuftdatenApi;
import nl.bertriksikken.luftdaten.LuftdatenConfig;
import nl.bertriksikken.luftdaten.LuftdatenUploader;
import nl.bertriksikken.nbiot.NbIotReceiver;
import nl.bertriksikken.opensense.IOpenSenseRestApi;
import nl.bertriksikken.opensense.OpenSenseConfig;
import nl.bertriksikken.opensense.OpenSenseUploader;
import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.SensorData;
import nl.bertriksikken.ttn.ETtnStackVersion;
import nl.bertriksikken.ttn.MqttListener;
import nl.bertriksikken.ttn.TtnAppConfig;
import nl.bertriksikken.ttn.TtnConfig;
import nl.bertriksikken.ttnv3.enddevice.EndDevice;
import nl.bertriksikken.ttnv3.enddevice.EndDeviceRegistry;
import nl.bertriksikken.ttnv3.enddevice.IEndDeviceRegistryRestApi;

public final class LoraLuftdatenForwarder {

    private static final Logger LOG = LoggerFactory.getLogger(LoraLuftdatenForwarder.class);
    private static final String CONFIG_FILE = "loraluftdatenforwarder.yaml";

    private final NbIotReceiver nbIotReceiver;
    private final List<MqttListener> mqttListeners = new ArrayList<>();
    private final LuftdatenUploader luftdatenUploader;
    private final OpenSenseUploader openSenseUploader;
    private final Map<String, EndDeviceRegistry> deviceRegistries = new HashMap<>();
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
        ILuftdatenApi restClient = LuftdatenUploader.newRestClient(luftdatenConfig.getUrl(),
                Duration.ofSeconds(luftdatenConfig.getTimeout()));
        luftdatenUploader = new LuftdatenUploader(restClient);

        OpenSenseConfig openSenseConfig = config.getOpenSenseConfig();
        IOpenSenseRestApi openSenseClient = OpenSenseUploader.newRestClient(openSenseConfig.getUrl(),
                Duration.ofSeconds(openSenseConfig.getTimeoutSec()));
        openSenseUploader = new OpenSenseUploader(config.getOpenSenseConfig().getIds(), openSenseClient);

        TtnConfig ttnConfig = config.getTtnConfig();
        for (TtnAppConfig appConfig : config.getTtnConfig().getApps()) {
            // add listener for each app
            EPayloadEncoding encoding = appConfig.getEncoding();
            LOG.info("Adding MQTT listener for TTN application '{}' with encoding '{}'", appConfig.getName(), encoding);
            MqttListener listener = new MqttListener(ttnConfig, appConfig,
                    (deviceEui, payload) -> messageReceived(encoding, deviceEui, payload));
            mqttListeners.add(listener);

            // for each TTN v3 app, create a device registry client so we can look up
            // attributes
            if (appConfig.getVersion() == ETtnStackVersion.V3) {
                IEndDeviceRegistryRestApi restApi = EndDeviceRegistry.newRestClient(ttnConfig.getIdentityServerUrl(),
                        Duration.ofSeconds(ttnConfig.getIdentityServerTimeout()));
                String appName = appConfig.getName();
                EndDeviceRegistry deviceRegistry = new EndDeviceRegistry(restApi, appName, appConfig.getKey());
                deviceRegistries.put(appName, deviceRegistry);
            }
        }
    }

    private void messageReceived(EPayloadEncoding encoding, String deviceEui, byte[] payload) {
        LOG.info("Received: '{}'", payload);

        // decode and upload
        try {
            SensorData sensorData = decodeTtnMessage(encoding, payload);
            luftdatenUploader.scheduleUpload(deviceEui, sensorData);
            openSenseUploader.scheduleUpload(deviceEui, sensorData);
        } catch (PayloadParseException e) {
            LOG.warn("Could not parse '{}' payload from: '{}", encoding, payload);
        }
    }

    // package-private to allow testing
    SensorData decodeTtnMessage(EPayloadEncoding encoding, byte[] payload) throws PayloadParseException {
        SensorData sensorData = new SensorData();

        switch (encoding) {
        case TTN_ULM:
            TtnUlmMessage ulmMessage = new TtnUlmMessage();
            ulmMessage.parse(payload);
            sensorData.addValue(ESensorItem.PM10, ulmMessage.getPm10());
            sensorData.addValue(ESensorItem.PM2_5, ulmMessage.getPm2_5());
            sensorData.addValue(ESensorItem.HUMI, ulmMessage.getRhPerc());
            sensorData.addValue(ESensorItem.TEMP, ulmMessage.getTempC());
            break;
        case CAYENNE:
            TtnCayenneMessage cayenne = new TtnCayenneMessage();
            cayenne.parse(payload);
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
        executor.scheduleAtFixedRate(this::updateOpenSenseMapping, 0, 60, TimeUnit.MINUTES);

        // start opensense uploader
        openSenseUploader.start();

        luftdatenUploader.start();
        for (MqttListener listener : mqttListeners) {
            listener.start();
        }

        LOG.info("Started LoraLuftdatenForwarder application");
    }

    // retrieves application attributes and updates the device EUI -> opensense id mapping in the opensense uploader
    private void updateOpenSenseMapping() {
        for (Entry<String, EndDeviceRegistry> entry : deviceRegistries.entrySet()) {
            String applicationId = entry.getKey();
            EndDeviceRegistry registry = entry.getValue();
            LOG.info("Fetching TTNv3 application attributes for '{}'", applicationId);
            try {
                List<EndDevice> devices = registry.listEndDevices();
                for (EndDevice device : devices) {
                    String devEui = device.getIds().getDevEui();
                    String opensenseId = device.getAttributes().getOrDefault("opensense-id", "");
                    if (!opensenseId.isBlank())
                        openSenseUploader.addMapping(devEui, opensenseId);
                }
            } catch (IOException e) {
                LOG.warn("Error getting opensense map ids for {}", e.getMessage());
            }
            LOG.info("Fetching TTNv3 application attributes done");
        }
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
