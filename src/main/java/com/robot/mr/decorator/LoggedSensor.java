package com.robot.mr.decorator;

import java.util.logging.Logger;

import com.robot.mr.interfaces.ISensor;
import com.robot.mr.model.SensorData;

public class LoggedSensor extends SensorDecorator {
	private static final Logger logger = Logger.getLogger(LoggedSensor.class.getName());
	
	public LoggedSensor(ISensor wrappedSensor) {
		super(wrappedSensor);
	}
	
	@Override
	public SensorData readData() {
		SensorData data = super.readData();
		log(data);
		return data;
	}
	
	private void log(SensorData data) {
        logger.info("Leitura do sensor [" + data.getSensorId() + "] " +
                    "tipo=" + data.getType() +
                    " valor=" + data.getValue() +
                    " unidade=" + data.getUnit() +
                    " timestamp=" + data.getTimestamp());
    }
}
