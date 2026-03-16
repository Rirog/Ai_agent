package org.example;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.api.OllamaApi;
import org.example.client.ClientManager;
import org.example.cmdStart.CmdStart;
import org.example.models.request.AiHistoryRequest;
import org.example.models.request.AiRequest;
import org.example.models.request.MessageRequest;
import org.example.models.response.AIResponse;
import org.example.models.response.AiHistoryResponse;
import retrofit2.Response;

import java.util.ArrayList;

public class AiModelRequest {
    private final OllamaApi client = ClientManager.createClient(OllamaApi.class);
    private final ArrayList<MessageRequest> chat = new ArrayList<>();
    private final boolean stream = false;
    private final boolean think = false;

    @SneakyThrows
    public void getAiResponse(String model, String message) {
        AiRequest request = new AiRequest(model, message, stream, think);
        CmdStart cmdStart = new CmdStart();

        Response<AIResponse> response = client.aiGenerate(request).execute();
        assert response.body() != null;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String json = response.body().getResponse();
        json = json.replace("```", "").replace("json", "");

        JsonNode jsonNode = objectMapper.readTree(json);
        JsonNode commands = jsonNode.get("commands");

        for (JsonNode cmd : commands) {
            switch (cmd.get("action").asText()) {
//                case "MERGE" -> ;
//                case "CREATE_BRANCH" -> ; думаб
                case "COMMIT" -> cmdStart.commitProject(cmd.get("parameters").get("message").asText());
//                case "PUSH" -> ;
//                case "PULL" -> ;
//                case "STATUS" -> ;
//                case "LOG" -> ;
            }
        }

    }

    @SneakyThrows
    public void getChatAiResponse(String model, String message) {
        String role = "user";
        MessageRequest messageRequest = new MessageRequest(role, message);

        chat.add(messageRequest);

        AiHistoryRequest request = new AiHistoryRequest(model, stream, chat, think);

        Response<AiHistoryResponse> response = client.aiChat(request).execute();

        assert response.body() != null;
        System.out.println(response.body().getMessage().getContent());
    }
//Привет прочто запомни эту комбинация чисел 1 2 3245 246 и не надо мне отвечать длиннымми сообщениями пиши кратко и по существу
}
