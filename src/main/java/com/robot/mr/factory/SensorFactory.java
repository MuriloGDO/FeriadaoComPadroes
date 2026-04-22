package com.robot.mr.factory;

import com.robot.mr.enums.SensorType;
import com.robot.mr.interfaces.ISensor;

public class SensorFactory {
	public static ISensor create(SensorType type, String id) {
		return switch (type) {
			case PIR -> new PIRSensor(id);
			case ULTRASONIC -> new UltrasonicSensor(id);
			case TEMPERATURE -> new TemperatureSensor(id);
			case CAMERA -> new CameraSensor(id);
		};
	}
}
