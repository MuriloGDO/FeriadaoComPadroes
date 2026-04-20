package com.robot.mr.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.robot.mr.enums.SensorType;

// Dados lidos de um sensor em um determinado tempo
public class SensorData {
	private String sensorId;
	private SensorType type;
	private double value;
	private String unit;
	private LocalDateTime timestamp;
	
	private static final ObjectMapper mapper = new ObjectMapper();

	public SensorData() {}

	public SensorData(String sensorId, SensorType type, double value, String unit, LocalDateTime timestamp) {
		super();
		this.sensorId = sensorId;
		this.type = type;
		this.value = value;
		this.unit = unit;
		this.timestamp = timestamp;
	}
	
	public String toJson() {
		try {
			return mapper.writeValueAsString(this);
		}
		catch (Exception e) {
			throw new RuntimeException("Erro ao converter SensorData para JSON", e);
		}
	}
	
	public static SensorData fromJson(String json) {
		try {
			return mapper.readValue(json, SensorData.class);
		}
		catch (Exception e) {
			throw new RuntimeException("Erro ao converter JSON para SensorData", e);
		}
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public SensorType getType() {
		return type;
	}

	public void setType(SensorType type) {
		this.type = type;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "SensorData [sensorId=" + sensorId + ", type=" + type + ", value=" + value + ", unit=" + unit
				+ ", timestamp=" + timestamp + "]";
	}
}
