package com.robot.mr.interfaces;

public interface IRobot {
	void moveFoward(int speed);
	void moveBackward(int speed);
	void turnLeft(int degrees);
	void turnRight(int degrees);
	void stop();
	
	void capturePhoto();
	void triggerAlert(String message);
}
