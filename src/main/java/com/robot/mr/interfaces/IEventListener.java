package com.robot.mr.interfaces;

import com.robot.mr.model.RobotEvent;

public interface IEventListener {
	void onEvent(RobotEvent event);
}
