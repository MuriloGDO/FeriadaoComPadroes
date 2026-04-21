package factory;

import com.robot.mr.enums.SensorType;
import com.robot.mr.interfaces.ISensor;
import com.robot.mr.model.SensorData;

public class TemperatureSensor implements ISensor {
	private final String id;
    private SensorData lastReading;
 
    public TemperatureSensor(String id) {
        this.id = id;
    }
 
    @Override
    public String getId() {
        return id;
    }
 
    @Override
    public SensorType getType() {
        return SensorType.TEMPERATURE;
    }
 
    @Override
    public SensorData readData() {
        if (lastReading == null) {
            return new SensorData(id, SensorType.TEMPERATURE, -273.15, "°C");
        }
        return lastReading;
    }
 
    public void updateReading(double celsius) {
        this.lastReading = new SensorData(id, SensorType.TEMPERATURE, celsius, "°C");
    }
}
