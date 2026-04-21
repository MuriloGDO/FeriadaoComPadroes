package com.robot.mr.decorator;

import java.util.logging.Logger;

import com.robot.mr.enums.EventType;
import com.robot.mr.enums.SensorType;
import com.robot.mr.interfaces.ISensor;
import com.robot.mr.model.RobotEvent;
import com.robot.mr.model.SensorData;
import com.robot.mr.observer.EventBus;

public class AlertSensor extends SensorDecorator {
	private static final Logger logger = Logger.getLogger(AlertSensor.class.getName());
	
    private static final double DEFAULT_PIR_THRESHOLD       = 0.0;
    private static final double DEFAULT_ULTRASONIC_THRESHOLD = 30.0;
    private static final double DEFAULT_TEMPERATURE_THRESHOLD = 30.0;
 
    private final double threshold;
 
    public AlertSensor(ISensor wrappedSensor) {
        super(wrappedSensor);
        this.threshold = defaultThresholdFor(wrappedSensor.getType());
    }

    private static double defaultThresholdFor(SensorType type) {
        return switch (type) {
            case ULTRASONIC -> DEFAULT_ULTRASONIC_THRESHOLD;
            case PIR -> DEFAULT_PIR_THRESHOLD;
            case TEMPERATURE -> DEFAULT_TEMPERATURE_THRESHOLD;
            default -> 0.0;
        };
    }
 
    public AlertSensor(ISensor wrappedSensor, double threshold) {
        super(wrappedSensor);
        this.threshold = threshold;
    }
 
    @Override
    public SensorData readData() {
        SensorData data = super.readData();
 
        // Verifica se o valor ultrapassa o limiar e dispara alerta se necessário
        checkThreshold(data);
 
        return data;
    }
 
    private void checkThreshold(SensorData data) {
        boolean shouldAlert = switch (data.getType()) {
            case PIR         -> data.getValue() > threshold;
            case ULTRASONIC  -> data.getValue() > 0 && data.getValue() < threshold;
            case TEMPERATURE -> data.getValue() > threshold;
            case CAMERA      -> false;
        };
 
        if (shouldAlert) {
            logger.warning("Limiar atingido no sensor [" + data.getSensorId() + "] " +
                           "valor=" + data.getValue());
 
            EventBus.getInstance().publish(
                RobotEvent.of(EventType.MOTION_ALERT, data.toJson(), data.getSensorId())
            );
        }
    }
}
