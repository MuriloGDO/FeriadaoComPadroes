package com.robot.mr.decorator;

import com.robot.mr.enums.SensorType;
import com.robot.mr.interfaces.ISensor;
import com.robot.mr.model.SensorData;

public abstract class SensorDecorator implements ISensor {
	protected final ISensor wrappedSensor;
	
	public SensorDecorator(ISensor wrappedSensor) {
		this.wrappedSensor = wrappedSensor;
	}
	
	@Override
	public String getId() {
		return wrappedSensor.getId();
	}
	
	@Override
	public SensorType getType() {
		return wrappedSensor.getType();
	}
	
	@Override
	public SensorData readData() {
		return wrappedSensor.readData();
	}
}
