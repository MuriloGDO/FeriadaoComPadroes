package com.robot.mr.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.robot.mr.enums.EventType;

public class RobotEvent {
	private UUID id;
	private EventType type;
	private String payload;
	private String source;
	private LocalDateTime timestamp;

	private RobotEvent(EventType type, String payload, String source) {
		this.id = UUID.randomUUID();
		this.type = type;
		this.payload = payload;
		this.source = source;
		this.timestamp = LocalDateTime.now();
	}
	
	public static RobotEvent of(EventType type, String payload) {
		return new RobotEvent(type, payload, "system");
	}
	
	public static RobotEvent of(EventType type, String payload, String source) {
		return new RobotEvent(type, payload, source);
	}

	public UUID getId() {
		return id;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "RobotEvent [id=" + id + ", type=" + type + ", payload=" + payload + ", source=" + source
				+ ", timestamp=" + timestamp + "]";
	}	
}
