package nl.bertriksikken.pm.json;

import java.util.ArrayList;

import nl.bertriksikken.pm.ESensorItem;

public final class JsonDecoderConfig extends ArrayList<JsonDecoderItem> {

    private static final long serialVersionUID = 1L;

    public JsonDecoderConfig() {
        // default well-known values from "Apeldoorn"
        add(new JsonDecoderItem("/pm10", ESensorItem.PM10));
        add(new JsonDecoderItem("/pm2p5", ESensorItem.PM2_5));
        add(new JsonDecoderItem("/rh", ESensorItem.HUMI));
        add(new JsonDecoderItem("/temp", ESensorItem.TEMP));

        // default well-known values from "LoraNoiseKit"
        add(new JsonDecoderItem("/la/min", ESensorItem.NOISE_LA_MIN));
        add(new JsonDecoderItem("/la/avg", ESensorItem.NOISE_LA_EQ));
        add(new JsonDecoderItem("/la/max", ESensorItem.NOISE_LA_MAX));
    }

    public JsonDecoderConfig(ArrayList<JsonDecoderItem> items) {
        addAll(items);
    }

}
