package nl.bertriksikken.soundkit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
final class NoiseJson {

    @JsonProperty("la")
    NoiseStatsJson la = new NoiseStatsJson();

    @JsonProperty("lc")
    NoiseStatsJson lc = new NoiseStatsJson();

    @JsonProperty("lz")
    NoiseStatsJson lz = new NoiseStatsJson();

    static final class NoiseStatsJson {

        @JsonProperty("min")
        double min = Double.NaN;
        @JsonProperty("max")
        double max = Double.NaN;
        @JsonProperty("avg")
        double avg = Double.NaN;
        @JsonProperty("spectrum")
        double[] spectrum = new double[0];
    }

}
