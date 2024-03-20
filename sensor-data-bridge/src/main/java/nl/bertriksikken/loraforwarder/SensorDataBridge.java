package nl.bertriksikken.loraforwarder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import nl.bertriksikken.gls.GeoLocationService;
import nl.bertriksikken.loraforwarder.util.CatchingRunnable;
import nl.bertriksikken.opensense.OpenSenseUploader;
import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.PayloadParseException;
import nl.bertriksikken.pm.SensorData;
import nl.bertriksikken.pm.cayenne.TtnCayenneMessage;
import nl.bertriksikken.pm.json.JsonDecoder;
import nl.bertriksikken.pm.sps30.Sps30Message;
import nl.bertriksikken.pm.ttnulm.TtnUlmMessage;
import nl.bertriksikken.senscom.SensComUploader;
import nl.bertriksikken.ttn.MqttListener;
import nl.bertriksikken.ttn.TtnAppConfig;
import nl.bertriksikken.ttn.TtnAppConfig.DecoderConfig;
import nl.bertriksikken.ttn.TtnConfig;
import nl.bertriksikken.ttn.TtnUplinkMessage;
import nl.bertriksikken.ttnv3.enddevice.EndDevice;
import nl.bertriksikken.ttnv3.enddevice.EndDeviceRegistry;
import nl.bertriksikken.ttnv3.enddevice.IEndDeviceRegistryRestApi;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.jar.Manifest;

public final class SensorDataBridge {

    private static final Logger LOG = LoggerFactory.getLogger(SensorDataBridge.class);
    private static final String CONFIG_FILE = "sensor-data-bridge.yaml";

    private final List<MqttListener> mqttListeners = new ArrayList<>();
    private final List<IUploader> uploaders = new ArrayList<>();
    private final GeoLocationService geoLocationService;
    private final Map<String, EndDeviceRegistry> deviceRegistries = new HashMap<>();
    private final Map<String, CommandHandler> commandHandlers = new HashMap<>();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final JsonDecoder jsonDecoder = new JsonDecoder();

    public static void main(String[] args) throws IOException, MqttException {
        PropertyConfigurator.configure("log4j.properties");

        SensorDataBridgeConfig config = readConfig(new File(CONFIG_FILE));
        SensorDataBridge app = new SensorDataBridge(config);
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
        app.start();
    }

    private SensorDataBridge(SensorDataBridgeConfig config) {
        String version = getVersion();
        LOG.info("Initializing SensorDataBridge application, version '{}'", version);

        if (!config.getSensComConfig().getUrl().isEmpty()) {
            uploaders.add(SensComUploader.create(config.getSensComConfig(), version));
        }
        if (!config.getOpenSenseConfig().getUrl().isEmpty()) {
            uploaders.add(OpenSenseUploader.create(config.getOpenSenseConfig()));
        }

        geoLocationService = GeoLocationService.create(config.getGeoLocationConfig());

        TtnConfig ttnConfig = config.getTtnConfig();
        for (TtnAppConfig appConfig : config.getTtnConfig().getApps()) {
            EPayloadEncoding encoding = appConfig.getDecoder().getEncoding();
            // add listener for each app
            LOG.info("Adding MQTT listener for TTN application '{}' with encoding '{}'", appConfig.getName(), encoding);
            MqttListener listener = new MqttListener(ttnConfig, appConfig,
                    uplink -> messageReceived(appConfig, uplink));
            mqttListeners.add(listener);

            // for each app, create a device registry client, so we can look up attributes
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
            SensorData sensorData = decodeTtnMessage(appConfig.getDecoder(), uplink);
            LOG.info("Decoded: '{}'", sensorData);
            uploaders.forEach(uploader -> uploader.scheduleUpload(appDeviceId, sensorData));
        } catch (PayloadParseException e) {
            LOG.warn("Could not parse payload from: '{}", uplink.getRawPayload());
        }
    }

    // package-private to allow testing
    SensorData decodeTtnMessage(DecoderConfig config, TtnUplinkMessage uplink) throws PayloadParseException {
        SensorData sensorData = new SensorData();

        // common fields
        if (Double.isFinite(uplink.getRSSI())) {
            sensorData.addValue(ESensorItem.LORA_RSSI, uplink.getRSSI());
        }
        if (Double.isFinite(uplink.getSNR())) {
            sensorData.addValue(ESensorItem.LORA_SNR, uplink.getSNR());
        }
        if (uplink.getSF() > 0) {
            sensorData.addValue(ESensorItem.LORA_SF, uplink.getSF());
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
        switch (config.getEncoding()) {
            case TTN_ULM:
                TtnUlmMessage ulmMessage = TtnUlmMessage.parse(uplink.getRawPayload());
                sensorData.addValue(ESensorItem.PM10, ulmMessage.getPm10());
                sensorData.addValue(ESensorItem.PM2_5, ulmMessage.getPm2_5());
                sensorData.addValue(ESensorItem.HUMIDITY, ulmMessage.getRhPerc());
                sensorData.addValue(ESensorItem.TEMPERATURE, ulmMessage.getTempC());
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
                    sensorData.addValue(ESensorItem.HUMIDITY, cayenne.getRhPerc());
                }
                if (cayenne.hasTempC()) {
                    sensorData.addValue(ESensorItem.TEMPERATURE, cayenne.getTempC());
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
            case JSON:
                try {
                    jsonDecoder.parse(config.getProperties(), uplink.getDecodedFields(), sensorData);
                } catch (JsonProcessingException e) {
                    throw new PayloadParseException(e);
                }
                break;
            default:
                throw new IllegalStateException("Unhandled encoding: " + config.getEncoding());
        }
        return sensorData;
    }

    /**
     * Starts the application.
     *
     * @throws MqttException in case of a problem starting MQTT client
     */
    private void start() throws MqttException {
        LOG.info("Starting sensor-data-bridge application");

        // schedule task to fetch opensense ids
        executor.scheduleAtFixedRate(new CatchingRunnable(LOG, this::updateAttributes), 0, 60, TimeUnit.MINUTES);

        // start uploaders
        uploaders.forEach(IUploader::start);

        // start listeners
        for (MqttListener listener : mqttListeners) {
            listener.start();
        }

        LOG.info("Started sensor-data-bridge application");
    }

    // retrieves application attributes and notifies each interested components
    private void updateAttributes() {
        // fetch all attributes
        for (Entry<String, EndDeviceRegistry> entry : deviceRegistries.entrySet()) {
            String applicationId = entry.getKey();
            EndDeviceRegistry registry = entry.getValue();
            LOG.info("Fetching TTNv3 application attributes for '{}'", applicationId);
            Map<String, AttributeMap> map = new HashMap<>();
            try {
                for (EndDevice device : registry.listEndDevices(IEndDeviceRegistryRestApi.FIELD_IDS,
                        IEndDeviceRegistryRestApi.FIELD_ATTRIBUTES)) {
                    map.put(device.getDeviceId(), new AttributeMap((device.getAttributes())));
                }
            } catch (IOException e) {
                LOG.warn("Error getting attributes for {}", applicationId, e);
            }
            // notify all uploaders
            uploaders.forEach(uploader -> uploader.scheduleProcessAttributes(applicationId, map));
        }
        LOG.info("Fetching TTNv3 application attributes done");
    }

    /**
     * Stops the application.
     */
    private void stop() {
        LOG.info("Stopping sensor-data-bridge application");

        executor.shutdownNow();
        mqttListeners.forEach(MqttListener::stop);
        commandHandlers.values().forEach(CommandHandler::stop);
        uploaders.forEach(IUploader::stop);

        LOG.info("Stopped sensor-data-bridge application");
    }

    private String getVersion() {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
        try {
            Manifest manifest = new Manifest(stream);
            String version = manifest.getMainAttributes().getValue("Implementation-Version");
            return version != null ? version : "unspecified";
        } catch (IOException e) {
            LOG.warn("Could not read version from manifest", e);
            return "unknown";
        }
    }

    private static SensorDataBridgeConfig readConfig(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        SensorDataBridgeConfig config = new SensorDataBridgeConfig();
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                return mapper.readValue(fis, SensorDataBridgeConfig.class);
            } catch (IOException e) {
                LOG.warn("Failed to load config {}, using defaults", file.getAbsoluteFile());
            }
        } else {
            LOG.warn("No config found, writing default configuration {}", file.getAbsoluteFile());
            mapper.writeValue(file, config);
        }
        return config;
    }

}
