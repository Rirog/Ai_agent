package org.example.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.api.OllamaApi;
import org.example.client.ClientManager;
import org.example.config.ConfigManager;
import org.example.dto.request.AiRequest;
import org.example.dto.response.AIResponse;
import org.example.dto.response.AiResult;
import org.example.service.AiService;
import retrofit2.Response;

public class AiServiceImpl implements AiService {

    private final String model = ConfigManager.getAiModelFast();
    private final OllamaApi ollamaApi = ClientManager.createClient(OllamaApi.class);

    @Override
    @SneakyThrows
    public AiResult generateResponse(String message) {
        boolean stream = false;
        boolean think = false;

        AiRequest aiRequest = new AiRequest(model, message, stream, think);

        Response<AIResponse> response = ollamaApi.aiGenerate(aiRequest).execute();
        assert response.body() != null;

        String email = response.body().getResponse();

        return parseResponse(email);
    }

    @SneakyThrows
    private AiResult parseResponse(String email) {
        email = cleanJson(email);
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(email, AiResult.class);
    }

    @SneakyThrows
    private String cleanJson(String email) {
        return email
                .replaceAll("```", "")
                .replaceAll("json", "");
    }
}
