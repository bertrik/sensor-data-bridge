package nl.bertriksikken.loraforwarder.rudzl.dto;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public final class RudzlMessage {

	private final ImmutableMap<String, Object> fields;

	public RudzlMessage(Map<String, Object> fields) {
		this.fields = ImmutableMap.copyOf(fields);
	}
	
	public double getPM10() {
		return (double)fields.getOrDefault("PM10_Avg", Double.NaN);
	}
	
	public double getPM2_5() {
		return (double)fields.getOrDefault("PM25_Avg", Double.NaN);
	}

}
