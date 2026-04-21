package factory;

import com.robot.mr.enums.SensorType;
import com.robot.mr.interfaces.ISensor;
import com.robot.mr.model.SensorData;

public class UltrasonicSensor implements ISensor {
	private final String id;
	private SensorData lastReading;
	
	public UltrasonicSensor(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public SensorType getType() {
		return SensorType.ULTRASONIC;
	}

	@Override
	public SensorData readData() {
		if (lastReading == null) {
			return new SensorData(id, SensorType.ULTRASONIC, -1.0, "cm");
		}
		return lastReading;
	}
	
	public void updateReading(double distanceCm) {
		this.lastReading = new SensorData(id, SensorType.ULTRASONIC, distanceCm, "cm");
	}	
}
