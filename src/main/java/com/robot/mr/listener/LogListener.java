package com.robot.mr.listener;

import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.robot.mr.interfaces.IEventListener;
import com.robot.mr.model.RobotEvent;

@Component
public class LogListener implements IEventListener {
	private static final Logger logger = Logger.getLogger(LogListener.class.getName());
	 
    @Override
    public void onEvent(RobotEvent event) {
        logger.info("EVENTO [" + event.getType() + "] " +
                    "origem=" + event.getSource() + " " +
                    "payload=" + event.getPayload() + " " +
                    "timestamp=" + event.getTimestamp());
    }
}
