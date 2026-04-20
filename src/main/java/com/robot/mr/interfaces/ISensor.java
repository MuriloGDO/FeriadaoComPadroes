package com.robot.mr.interfaces;

import com.robot.mr.enums.SensorType;
import com.robot.mr.model.SensorData;

public interface ISensor {
	String getId();
	SensorType getType();
	SensorData readData();
}
