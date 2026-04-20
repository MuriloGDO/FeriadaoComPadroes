package com.robot.mr.model;

import com.robot.mr.enums.Direction;

public class OpenAIResponse {
	private String action;
	private Direction direction;
	private int speed;
	private String message;
	private boolean triggerAlert;
	private boolean capturePhoto;
	
	public OpenAIResponse() {}

	public OpenAIResponse(String action, Direction direction, int speed, String message, boolean triggerAlert,
			boolean capturePhoto) {
		super();
		this.action = action;
		this.direction = direction;
		this.speed = speed;
		this.message = message;
		this.triggerAlert = triggerAlert;
		this.capturePhoto = capturePhoto;
	}
	
	public static OpenAIResponse idle() {
        return new OpenAIResponse("IDLE", Direction.STOPPED, 0, "Sem ação necessária", false, false);
    }

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isTriggerAlert() {
		return triggerAlert;
	}

	public void setTriggerAlert(boolean triggerAlert) {
		this.triggerAlert = triggerAlert;
	}

	public boolean isCapturePhoto() {
		return capturePhoto;
	}

	public void setCapturePhoto(boolean capturePhoto) {
		this.capturePhoto = capturePhoto;
	}

	@Override
	public String toString() {
		return "OpenAIResponse [action=" + action + ", direction=" + direction + ", speed=" + speed + ", message="
				+ message + ", triggerAlert=" + triggerAlert + ", capturePhoto=" + capturePhoto + "]";
	}	
}
