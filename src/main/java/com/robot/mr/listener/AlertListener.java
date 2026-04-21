package com.robot.mr.listener;

import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.robot.mr.interfaces.IEventListener;
import com.robot.mr.model.RobotEvent;

@Component
public class AlertListener implements IEventListener {
	private static final Logger logger = Logger.getLogger(AlertListener.class.getName());
	 
    @Override
    public void onEvent(RobotEvent event) {
        switch (event.getType()) {
            case MOTION_ALERT -> {
                logger.warning("ALERTA DE MOVIMENTO detectado — " +
                               "origem=" + event.getSource() + " " +
                               "dados=" + event.getPayload());
            }
            case COMMUNICATION_ERROR -> {
                logger.severe("ERRO DE COMUNICAÇÃO com ESP32 — " +
                              "detalhe=" + event.getPayload());
            }
            case ESP32_OFFLINE -> {
                logger.warning("ESP32 DESCONECTADO — sistema em modo degradado");
            }
            default -> {}
        }
    }
}
