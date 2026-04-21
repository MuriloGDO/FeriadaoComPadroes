package com.robot.mr.websocket;

import java.util.logging.Logger;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robot.mr.enums.EventType;
import com.robot.mr.model.RobotEvent;
import com.robot.mr.observer.EventBus;

import singleton.WifiConnectionManager;

@Component
public class RobotWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = Logger.getLogger(RobotWebSocketHandler.class.getName());
 
    private final ObjectMapper mapper = new ObjectMapper();
    private WebSocketSession esp32Session;
 
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        this.esp32Session = session;
        WifiConnectionManager.getInstance().setConnected(true);
        logger.info("ESP32 conectado — endereço: " + session.getRemoteAddress());
 
        EventBus.getInstance().publish(
            RobotEvent.of(EventType.ESP32_ONLINE, session.getRemoteAddress().toString(), "websocket")
        );
    }
 
    // Formato esperado do ESP32:
    // sensor:  {"tipo":"SENSOR","pir":1,"dist":42,"temp":27.5}
    // foto:    {"tipo":"PHOTO","base64":"/9j/4AAQSkZJRg..."}
    // status:  {"tipo":"STATUS","uptime":3600,"rssi":-65}
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String json = message.getPayload();
        logger.info("Mensagem recebida do ESP32: " + json);
 
        try {
            JsonNode node = mapper.readTree(json);
            String tipo   = node.has("tipo") ? node.get("tipo").asText() : "UNKNOWN";
 
            switch (tipo) {
                case "SENSOR" -> handleSensorData(node, json);
                case "PHOTO"  -> handlePhoto(node);
                case "STATUS" -> handleStatus(json);
                default       -> logger.warning("Tipo de mensagem desconhecido: " + tipo);
            }
 
        } catch (Exception e) {
            logger.severe("Erro ao processar mensagem do ESP32: " + e.getMessage());
            EventBus.getInstance().publish(
                RobotEvent.of(EventType.COMMUNICATION_ERROR, e.getMessage(), "websocket")
            );
        }
    }
 
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        this.esp32Session = null;
        WifiConnectionManager.getInstance().setConnected(false);
        logger.warning("ESP32 desconectado — status: " + status);
 
        EventBus.getInstance().publish(
            RobotEvent.of(EventType.ESP32_OFFLINE, status.toString(), "websocket")
        );
    }
 
    private void handleSensorData(JsonNode node, String rawJson) {
        EventBus.getInstance().publish(
            RobotEvent.of(EventType.SENSOR_DATA, rawJson, "esp32")
        );
    }
 
    // Extrai o base64 da foto e publica como payload do evento PHOTO_CAPTURED.
    // O CameraProxy escuta esse evento e atualiza seu cache com o base64 real.
    private void handlePhoto(JsonNode node) {
        if (!node.has("base64")) {
            logger.warning("Mensagem PHOTO recebida sem campo base64");
            return;
        }
 
        String base64 = node.get("base64").asText();
        logger.info("Foto recebida do ESP32 — tamanho base64: " + base64.length() + " chars");
 
        EventBus.getInstance().publish(
            RobotEvent.of(EventType.PHOTO_CAPTURED, base64, "esp32")
        );
    }
 
    private void handleStatus(String rawJson) {
        EventBus.getInstance().publish(
            RobotEvent.of(EventType.COMMAND_EXECUTED, rawJson, "esp32")
        );
    }
 
    public void sendToESP32(String json) throws Exception {
        if (esp32Session == null || !esp32Session.isOpen()) {
            throw new IllegalStateException("ESP32 não está conectado");
        }
        esp32Session.sendMessage(new TextMessage(json));
        logger.info("Comando enviado ao ESP32: " + json);
    }
 
    public boolean isConnected() {
        return esp32Session != null && esp32Session.isOpen();
    }
}
