package nl.bertriksikken.loraforwarder.rudzl.dto;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public final class RudzlMessage {

	private final ImmutableMap<String, Object> fields;

	public RudzlMessage(Map<String, Object> fields) {
		this.fields = ImmutableMap.copyOf(fields);
	}
	
	public double getPM10() {
		Number number = (Number) fields.getOrDefault("PM10_Avg", Double.NaN);
		return number.doubleValue();
	}
	
	public double getPM2_5() {
		Number number = (Number) fields.getOrDefault("PM25_Avg", Double.NaN);
		return number.doubleValue();
	}

	public double getT() {
		Number number = (Number) fields.getOrDefault("T", Double.NaN);
		return number.doubleValue();
	}

	public double getRH() {
		Number number = (Number) fields.getOrDefault("RH", Double.NaN);
		return number.doubleValue();
	}

	public double getP() {
		Number number = (Number) fields.getOrDefault("P", Double.NaN);
		return number.doubleValue();
	}

}
