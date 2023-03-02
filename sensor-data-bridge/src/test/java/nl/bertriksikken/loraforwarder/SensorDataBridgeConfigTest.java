package nl.bertriksikken.loraforwarder;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.json.JsonDecoderConfig;
import nl.bertriksikken.pm.json.JsonDecoderItem;
import nl.bertriksikken.ttn.TtnAppConfig;
import nl.bertriksikken.ttn.TtnAppConfig.DecoderConfig;
import nl.bertriksikken.ttn.TtnConfig;

public final class SensorDataBridgeConfigTest {

    private final ObjectMapper mapper = new YAMLMapper();

    @Test
    public void testDefaults() throws JsonProcessingException {
        SensorDataBridgeConfig config = new SensorDataBridgeConfig();
        String text = mapper.writeValueAsString(config);
        System.out.println(text);
    }

    @Test
    public void testExample() throws JsonProcessingException {
        SensorDataBridgeConfig config = new SensorDataBridgeConfig();
        TtnConfig ttnConfig = new TtnConfig();
        TtnAppConfig pmApp = new TtnAppConfig("particulatematter", "secret", createParticulateMatterDecoder());
        ttnConfig.addApp(pmApp);
        TtnAppConfig apeldoornApp = new TtnAppConfig("ttn-apeldoorn-hittestress", "secret", createApeldoornDecoder());
        ttnConfig.addApp(apeldoornApp);
        TtnAppConfig soundkitApp = new TtnAppConfig("ttn-soundkit", "secret", createSoundkitDecoder());
        ttnConfig.addApp(soundkitApp);
        TtnAppConfig mjsApp = new TtnAppConfig("meet-je-stad", "secret", createMjsDecoder());
        ttnConfig.addApp(mjsApp);
        config.setTtnConfig(ttnConfig);
        String text = mapper.writeValueAsString(config);
        System.out.println(text);
    }

    private DecoderConfig createParticulateMatterDecoder() {
        return new DecoderConfig(EPayloadEncoding.CAYENNE, new TextNode(""));
    }

    private DecoderConfig createApeldoornDecoder() {
        JsonDecoderConfig jsonConfig = new JsonDecoderConfig();
        jsonConfig.add(new JsonDecoderItem("/pm10", ESensorItem.PM10));
        jsonConfig.add(new JsonDecoderItem("/pm2p5", ESensorItem.PM2_5));
        jsonConfig.add(new JsonDecoderItem("/rh", ESensorItem.HUMI));
        jsonConfig.add(new JsonDecoderItem("/temp", ESensorItem.TEMP));
        return new DecoderConfig(EPayloadEncoding.JSON, new POJONode(jsonConfig));
    }

    private DecoderConfig createSoundkitDecoder() {
        JsonDecoderConfig jsonConfig = new JsonDecoderConfig();
        jsonConfig.add(new JsonDecoderItem("/la/min", ESensorItem.NOISE_LA_MIN));
        jsonConfig.add(new JsonDecoderItem("/la/avg", ESensorItem.NOISE_LA_EQ));
        jsonConfig.add(new JsonDecoderItem("/la/max", ESensorItem.NOISE_LA_MAX));
        return new DecoderConfig(EPayloadEncoding.JSON, new POJONode(jsonConfig));
    }

    private DecoderConfig createMjsDecoder() {
        JsonDecoderConfig jsonConfig = new JsonDecoderConfig();
        jsonConfig.add(new JsonDecoderItem("/temperature", ESensorItem.TEMP));
        jsonConfig.add(new JsonDecoderItem("/humidity", ESensorItem.HUMI));
        jsonConfig.add(new JsonDecoderItem("/pm10", ESensorItem.PM10));
        jsonConfig.add(new JsonDecoderItem("/pm2_5", ESensorItem.PM2_5));
        return new DecoderConfig(EPayloadEncoding.JSON, new POJONode(jsonConfig));
    }
}
