package com.robot.mr.proxy;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.robot.mr.enums.EventType;
import com.robot.mr.interfaces.IEventListener;
import com.robot.mr.interfaces.IRobot;
import com.robot.mr.model.RobotEvent;

@Component
public class CameraProxy implements IRobot, IEventListener {
	private static final Logger logger = Logger.getLogger(CameraProxy.class.getName());
	
	private static final int MIN_INTERVAL_SECONDS = 10;
	private final IRobot robot;
	private String cachedPhoto;
	private LocalDateTime lastCaptureTs;
	private boolean authorized;
	
	public CameraProxy(IRobot robot) {
		this.robot = robot;
		this.authorized = false;
	}

	@Override
    public void onEvent(RobotEvent event) {
        if (event.getType() == EventType.PHOTO_CAPTURED) {
            this.cachedPhoto   = event.getPayload();
            this.lastCaptureTs = LocalDateTime.now();
            logger.info("Cache da câmera atualizado — foto recebida do ESP32 em " + lastCaptureTs);
        }
    }
	
	@Override
	public void moveFoward(int speed) {
		this.robot.moveFoward(speed);
	}

	@Override
	public void moveBackward(int speed) {
		this.robot.moveBackward(speed);
	}

	@Override
	public void turnLeft(int degrees) {
		this.robot.turnLeft(degrees);
	}

	@Override
	public void turnRight(int degrees) {
		this.robot.turnRight(degrees);
	}

	@Override
	public void stop() {
		this.robot.stop();
	}

	@Override
	public void capturePhoto() {
		checkAuthorization();
		
		if (isCacheValid()) {
			logger.info("Retornando foto do cache — última captura: " + lastCaptureTs);
			return;
		}
		logger.info("Cache expirado ou inexistente — solicitando nova foto ao ESP32");
        robot.capturePhoto();
    }

	@Override
	public void triggerAlert(String message) {
		this.robot.triggerAlert(message);
	}
	
	public String getCachedPhoto() {
        return cachedPhoto;
    }

	public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
        logger.info("Acesso à câmera " + (authorized ? "autorizado" : "revogado"));
    }
 
    public boolean isAuthorized() {
        return authorized;
    }
 
    private boolean isCacheValid() {
        if (cachedPhoto == null || lastCaptureTs == null) return false;
 
        LocalDateTime expiry = lastCaptureTs.plusSeconds(MIN_INTERVAL_SECONDS);
        return LocalDateTime.now().isBefore(expiry);
    }
 
    // Lança exceção se a câmera não estiver autorizada
    private void checkAuthorization() {
        if (!authorized) {
            logger.warning("Tentativa de acesso não autorizado à câmera bloqueada pelo Proxy");
            throw new SecurityException("Acesso à câmera não autorizado");
        }
    }
}
