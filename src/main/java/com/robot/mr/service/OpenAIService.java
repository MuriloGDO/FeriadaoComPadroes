package com.robot.mr.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robot.mr.enums.Direction;
import com.robot.mr.model.OpenAIResponse;

import okhttp3.*;

@Service
public class OpenAIService {
	private static final Logger logger = Logger.getLogger(OpenAIService.class.getName());
	 
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON  = MediaType.get("application/json");
 
    private static final String SYSTEM_PROMPT = """
            Você controla um robô de vigilância com ESP32.
            Analise o evento recebido e responda APENAS com JSON válido, sem texto adicional, no formato:
            {
              "action": "MOVE | STOP | ALERT | PHOTO | IDLE",
              "direction": "FORWARD | BACKWARD | LEFT | RIGHT | STOPPED",
              "speed": 0,
              "message": "descrição curta da decisão",
              "triggerAlert": false,
              "capturePhoto": false
            }
            """;
 
    private final OkHttpClient httpClient;
    private final ObjectMapper mapper;
 
    @Value("${robot.openai.key}")
    private String apiKey;
 
    @Value("${robot.openai.model:gpt-4o}")
    private String model;
 
    public OpenAIService() {
        this.httpClient = new OkHttpClient();
        this.mapper     = new ObjectMapper();
    }
 
    public OpenAIResponse analyzeEvent(String eventDescription) {
        logger.info("Enviando evento para OpenAI: " + eventDescription);
 
        String payload = buildTextPayload(eventDescription);
        return callAPI(payload);
    }
 
    public OpenAIResponse analyzeImage(String base64Image) {
        logger.info("Enviando imagem para OpenAI Vision");
 
        String payload = buildImagePayload(base64Image);
        return callAPI(payload);
    }
 
    private OpenAIResponse callAPI(String payload) {
        Request request = new Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(payload, JSON))
            .build();
 
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.severe("Erro na API OpenAI — status: " + response.code());
                return OpenAIResponse.idle();
            }
 
            String body = response.body().string();
            return parseResponse(body);
 
        } catch (Exception e) {
            logger.severe("Falha ao chamar API OpenAI: " + e.getMessage());
            return OpenAIResponse.idle();
        }
    }
 
    private OpenAIResponse parseResponse(String responseBody) {
        try {
            JsonNode root    = mapper.readTree(responseBody);
            String content   = root
                .path("choices").get(0)
                .path("message")
                .path("content")
                .asText();
 
            logger.info("Resposta da OpenAI: " + content);
 
            JsonNode json    = mapper.readTree(content);
            OpenAIResponse r = new OpenAIResponse();
 
            r.setAction(json.path("action").asText("IDLE"));
            r.setDirection(parseDirection(json.path("direction").asText("STOPPED")));
            r.setSpeed(json.path("speed").asInt(0));
            r.setMessage(json.path("message").asText(""));
            r.setTriggerAlert(json.path("triggerAlert").asBoolean(false));
            r.setCapturePhoto(json.path("capturePhoto").asBoolean(false));
 
            return r;
 
        } catch (Exception e) {
            logger.severe("Erro ao parsear resposta da OpenAI: " + e.getMessage());
            return OpenAIResponse.idle();
        }
    }
 
    private String buildTextPayload(String userMessage) {
        return """
                {
                  "model": "%s",
                  "messages": [
                    { "role": "system", "content": %s },
                    { "role": "user",   "content": %s }
                  ]
                }
                """.formatted(
                    model,
                    toJsonString(SYSTEM_PROMPT),
                    toJsonString(userMessage)
                );
    }
 
    private String buildImagePayload(String base64Image) {
        return """
                {
                  "model": "%s",
                  "messages": [
                    { "role": "system", "content": %s },
                    {
                      "role": "user",
                      "content": [
                        {
                          "type": "image_url",
                          "image_url": { "url": "data:image/jpeg;base64,%s" }
                        },
                        { "type": "text", "text": "Analise a imagem e decida a ação do robô." }
                      ]
                    }
                  ]
                }
                """.formatted(model, toJsonString(SYSTEM_PROMPT), base64Image);
    }
 
    private Direction parseDirection(String value) {
        try {
            return Direction.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Direction.STOPPED;
        }
    }
 
    private String toJsonString(String value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            return "\"\"";
        }
    }
}
