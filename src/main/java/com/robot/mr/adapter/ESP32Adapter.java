package com.robot.mr.adapter;

import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.robot.mr.interfaces.IRobot;
import com.robot.mr.model.RobotCommand;
import com.robot.mr.websocket.RobotWebSocketHandler;

@Component
public class ESP32Adapter implements IRobot {
	private static final Logger logger = Logger.getLogger(ESP32Adapter.class.getName());
	
	private final RobotWebSocketHandler wsHandler;

	public ESP32Adapter(RobotWebSocketHandler wsHandler) {
		this.wsHandler = wsHandler;
	}

	@Override
	public void moveFoward(int speed) {
		logger.info("Movendo para frente — velocidade: " + speed);
        send(new RobotCommand("MOVE")
            .withParam("dir", "FORWARD")
            .withParam("speed", clampSpeed(speed))
        );
	}

	@Override
	public void moveBackward(int speed) {
		logger.info("Movendo para trás — velocidade: " + speed);
        send(new RobotCommand("MOVE")
            .withParam("dir", "BACKWARD")
            .withParam("speed", clampSpeed(speed))
        );
	}

	@Override
	public void turnLeft(int degrees) {
		logger.info("Virando à esquerda — graus: " + degrees);
        send(new RobotCommand("TURN")
            .withParam("dir", "LEFT")
            .withParam("degrees", degrees)
        );
	}

	@Override
	public void turnRight(int degrees) {
		logger.info("Virando à direita — graus: " + degrees);
        send(new RobotCommand("TURN")
            .withParam("dir", "RIGHT")
            .withParam("degrees", degrees)
        );
	}

	@Override
	public void stop() {
		logger.info("Parando o robô");
        send(new RobotCommand("STOP"));
	}

	@Override
	public void capturePhoto() {
		logger.info("Solicitando captura de foto ao ESP32");
		send(new RobotCommand("PHOTO"));
	}

	@Override
	public void triggerAlert(String message) {
		logger.info("Disparando alerta: " + message);
		send(new RobotCommand("ALERT").withParam("msg", message));		
	}
	
	private void send(RobotCommand command) {
		try {
			wsHandler.sendToESP32(command.toJson());
		}
		catch (Exception e) {
			throw new RuntimeException("Erro ao enviar comando ao ESP32: " + e.getMessage(), e);
		}
	}
	
	// Garante que a velocidade sempre fique entre 0 e 100
    private int clampSpeed(int speed) {
        return Math.max(0, Math.min(100, speed));
    }
}
