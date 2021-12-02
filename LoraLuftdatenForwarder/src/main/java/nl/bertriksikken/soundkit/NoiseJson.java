package nl.bertriksikken.soundkit;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

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

        @Override
        public String toString() {
            String formattedSpectrum = Arrays.stream(spectrum).mapToObj(v -> String.format(Locale.ROOT, "%.1f", v))
                    .collect(Collectors.joining(",", "[", "]"));
            return String.format("{min=%.1f,max=%.1f,avg=%.1f,spectrum=%s}", min, max, avg, formattedSpectrum);
        }
    }

}
