package factory;

import com.robot.mr.enums.SensorType;
import com.robot.mr.interfaces.ISensor;
import com.robot.mr.model.SensorData;

public class PIRSensor implements ISensor {
	private final String id;
	private SensorData lastReading;
	
	public PIRSensor(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public SensorType getType() {
		return SensorType.PIR;
	}

	@Override
	public SensorData readData() {
		if (lastReading == null) {
			return new SensorData(id, SensorType.PIR, 0.0, "binary");
		}
		return lastReading;
	}
	
	public void updateReading(double value) {
		this.lastReading = new SensorData(id, SensorType.PIR, value, "binary");
	}	
}
