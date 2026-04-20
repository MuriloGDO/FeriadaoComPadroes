package com.robot.mr.interfaces;

public interface IRobot {
	void moveFoward(int speed);
	void moveBackward(int speed);
	void turnLeft(int degrees);
	void turnRight(int degrees);
	void stop();
	
	String capturePhoto();
	void triggerAlert(String message);
}
