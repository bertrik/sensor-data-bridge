package nl.bertriksikken.ttnv3.enddevice;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class FieldMask {

    @JsonProperty("paths")
    List<String> paths = new ArrayList<>();
    
    FieldMask(List<String> fields) {
        paths.addAll(fields);
    }
    
}
