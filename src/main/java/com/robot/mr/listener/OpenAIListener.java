package com.robot.mr.listener;

import java.util.function.Consumer;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.robot.mr.interfaces.IEventListener;
import com.robot.mr.model.OpenAIResponse;
import com.robot.mr.model.RobotEvent;
import com.robot.mr.service.OpenAIService;

@Component
public class OpenAIListener implements IEventListener {
	private static final Logger logger = Logger.getLogger(OpenAIListener.class.getName());
	 
    private final OpenAIService openAIService;
    private Consumer<OpenAIResponse> commandHandler;
 
    public OpenAIListener(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }
 
    public void setCommandHandler(Consumer<OpenAIResponse> handler) {
        this.commandHandler = handler;
    }
 
    @Override
    public void onEvent(RobotEvent event) {
        if (commandHandler == null) {
            logger.warning("CommandHandler não registrado — resposta da OpenAI será ignorada");
            return;
        }
 
        switch (event.getType()) {
            case SENSOR_DATA  -> handleSensorData(event);
            case PHOTO_CAPTURED -> handlePhoto(event);
            default -> {}
        }
    }
 
    private void handleSensorData(RobotEvent event) {
        logger.info("Enviando dados do sensor para OpenAI: " + event.getPayload());
 
        OpenAIResponse response = openAIService.analyzeEvent(
            "Evento do robô: " + event.getPayload() +
            " | Origem: " + event.getSource() +
            " | Timestamp: " + event.getTimestamp()
        );
 
        logger.info("Resposta da OpenAI: " + response);
        commandHandler.accept(response);
    }
 
    private void handlePhoto(RobotEvent event) {
        logger.info("Enviando imagem para OpenAI Vision");
 
        OpenAIResponse response = openAIService.analyzeImage(event.getPayload());
 
        logger.info("Resposta da OpenAI Vision: " + response);
        commandHandler.accept(response);
    }
}
