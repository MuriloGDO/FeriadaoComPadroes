package com.robot.mr.facade;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.robot.mr.enums.Direction;
import com.robot.mr.enums.EventType;
import com.robot.mr.listener.AlertListener;
import com.robot.mr.listener.LogListener;
import com.robot.mr.listener.OpenAIListener;
import com.robot.mr.model.OpenAIResponse;
import com.robot.mr.model.RobotEvent;
import com.robot.mr.observer.EventBus;
import com.robot.mr.proxy.CameraProxy;

import singleton.WifiConnectionManager;

@Component
public class RobotController {
    private static final Logger logger = Logger.getLogger(RobotController.class.getName());
 
    private final CameraProxy camera;
    private final WifiConnectionManager wifiManager;
    private final EventBus eventBus;
 
    private boolean guardModeActive = false;
 
    public RobotController(CameraProxy camera,
                           LogListener logListener,
                           AlertListener alertListener,
                           OpenAIListener openAIListener) {
 
        this.camera      = camera;
        this.wifiManager = WifiConnectionManager.getInstance();
        this.eventBus    = EventBus.getInstance();
 
        // Registra o handler de comandos da OpenAI — evita dependência circular
        openAIListener.setCommandHandler(this::processAICommand);
 
        eventBus.subscribeAll(logListener);
        eventBus.subscribe(EventType.MOTION_ALERT,       alertListener);
        eventBus.subscribe(EventType.COMMUNICATION_ERROR, alertListener);
        eventBus.subscribe(EventType.ESP32_OFFLINE,       alertListener);
        eventBus.subscribe(EventType.SENSOR_DATA,         openAIListener);
        eventBus.subscribe(EventType.PHOTO_CAPTURED,      openAIListener);
    }
 
    public void move(Direction direction, int speed) {
        checkConnection();
        logger.info("Movendo robô — direção: " + direction + " velocidade: " + speed);
 
        try {
            switch (direction) {
                case FORWARD  -> camera.moveFoward(speed);
                case BACKWARD -> camera.moveBackward(speed);
                case LEFT     -> camera.turnLeft(speed);
                case RIGHT    -> camera.turnRight(speed);
                case STOPPED  -> camera.stop();
            }
 
            eventBus.publish(
                RobotEvent.of(EventType.COMMAND_EXECUTED,
                    "direction=" + direction + " speed=" + speed, "facade")
            );
 
        } catch (Exception e) {
            logger.severe("Erro ao mover robô: " + e.getMessage());
            eventBus.publish(
                RobotEvent.of(EventType.COMMUNICATION_ERROR, e.getMessage(), "facade")
            );
            throw e;
        }
    }
 
    public void stop() {
        checkConnection();
        logger.info("Parando robô");
 
        try {
            camera.stop();
            eventBus.publish(RobotEvent.of(EventType.COMMAND_EXECUTED, "STOP", "facade"));
        } catch (Exception e) {
            logger.severe("Erro ao parar robô: " + e.getMessage());
            eventBus.publish(
                RobotEvent.of(EventType.COMMUNICATION_ERROR, e.getMessage(), "facade")
            );
            throw e;
        }
    }
 
    public void takePhoto() {
        checkConnection();
        camera.setAuthorized(true);
        logger.info("Solicitando foto ao ESP32");
        camera.capturePhoto();
    }
 
    // Retorna a última foto em cache — pode ser null se ainda não chegou
    public String getCachedPhoto() {
        return camera.getCachedPhoto();
    }
 
    public void enableGuardMode() {
        checkConnection();
        guardModeActive = true;
        camera.setAuthorized(true);
        logger.info("Modo vigia ativado");
 
        eventBus.publish(
            RobotEvent.of(EventType.COMMAND_EXECUTED, "GUARD_MODE=ON", "facade")
        );
    }
 
    public void disableGuardMode() {
        guardModeActive = false;
        camera.setAuthorized(false);
        logger.info("Modo vigia desativado");
 
        eventBus.publish(
            RobotEvent.of(EventType.COMMAND_EXECUTED, "GUARD_MODE=OFF", "facade")
        );
    }
 
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("connected",   wifiManager.isConnected());
        status.put("esp32Address", wifiManager.getAddress());
        status.put("guardMode",   guardModeActive);
        status.put("cameraAuth",  camera.isAuthorized());
        status.put("hasPhoto",    camera.getCachedPhoto() != null);
        return status;
    }
 
    private void processAICommand(OpenAIResponse response) {
        logger.info("Processando comando da OpenAI: " + response);
 
        if (!wifiManager.isConnected()) {
            logger.warning("ESP32 desconectado — comando da OpenAI ignorado");
            return;
        }
 
        if (response.isCapturePhoto()) {
            takePhoto();
        }
 
        if (response.isTriggerAlert()) {
            camera.triggerAlert(response.getMessage());
            eventBus.publish(
                RobotEvent.of(EventType.MOTION_ALERT, response.getMessage(), "openai")
            );
        }
 
        if (!"IDLE".equals(response.getAction()) && !"STOP".equals(response.getAction())) {
            move(response.getDirection(), response.getSpeed());
        } else if ("STOP".equals(response.getAction())) {
            stop();
        }
    }
 
    private void checkConnection() {
        if (!wifiManager.isConnected()) {
            throw new IllegalStateException("ESP32 não está conectado");
        }
    }
}
