package nl.bertriksikken.ttnv3.enddevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class EndDevices {

    @JsonProperty("end_devices")
    private final List<EndDevice> endDevices = new ArrayList<>();

    public List<EndDevice> getEndDevices() {
        return endDevices;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s", endDevices);
    }

}
