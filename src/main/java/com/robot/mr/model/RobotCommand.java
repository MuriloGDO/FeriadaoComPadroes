package com.robot.mr.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RobotCommand {
	private String cmd;
	private Map<String, Object> params;
	
	private static final ObjectMapper mapper = new ObjectMapper();

	public RobotCommand() {}

	public RobotCommand(String cmd) {
		this.cmd = cmd;
		this.params = new HashMap<>();
	}

	public RobotCommand(String cmd, Map<String, Object> params) {
		this.cmd = cmd;
		this.params = params;
	}
	
	public RobotCommand withParam(String key, Object value) {
		this.params.put(key, value);
		return this;
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

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "RobotCommand [cmd=" + cmd + ", params=" + params + "]";
	}
}
