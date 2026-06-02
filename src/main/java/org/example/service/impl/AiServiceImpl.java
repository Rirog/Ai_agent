package org.example.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.api.OllamaApi;
import org.example.client.ClientManager;
import org.example.config.ConfigManager;
import org.example.dto.request.AiHistoryRequest;
import org.example.dto.request.MessageRequest;
import org.example.dto.response.AIResponseHistory;
import org.example.dto.response.AiResult;
import org.example.exception.InvalidApiKeyException;
import org.example.security.ApiKeyManager;
import org.example.service.AiService;
import retrofit2.Response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class AiServiceImpl implements AiService {

    private final String model = ConfigManager.getOllamaModel();
    private OllamaApi ollamaApi;

    private final static String promptContext = """
            You are an email classification assistant.
            Analyze email content and classify it into one of the following categories:
            - emailMeeting
            - emailTask
            - emailSpam
            - emailOther
            Return ONLY valid JSON.
            Do not use markdown.
            Do not use code fences.
            Do not provide explanations.
            Do not provide additional text.
            Output format:
            {
            "type": "",
            "date": "",
            "time": "",
            "task": "",
            "summary": ""
            }
            Classification rules:
            emailMeeting:
            - meetings
            - calls
            - conferences
            - appointments
            - interviews
            Fill:
            - date
            - time
            - summary
            emailTask:
            - tasks
            - assignments
            - requests requiring action
            Fill:
            - task
            - summary
            - date only if explicitly mentioned
            emailSpam:
            - advertisements
            - marketing
            - promotions
            - newsletters
            Fill:
            - summary only
            emailOther:
            - all other emails
            Fill:
            - summary only
            Date format:
            YYYY-MM-DD
            Time format:
            HH:MM
            If date is not specified:
            date = ""
            If time is not specified:
            time = ""
            summary:
            Short title in Russian.
            Maximum 5 words.
            task:
            Task description in Russian.
            If the email contains relative dates:
            
            - today
            - tomorrow
            - the day after tomorrow
            - next week
            - next month
            - next Monday
            - next Tuesday
            - next Wednesday
            - next Thursday
            - next Friday
            - next Saturday
            - next Sunday
            
            Convert them to an absolute date using Today's date.
            
            Examples:
            
            Today: 2026-05-31
            
            "tomorrow"
            → 2026-06-01
            
            "next Monday"
            → 2026-06-01
            
            Always return the converted date in YYYY-MM-DD format.
            Always return valid JSON.
            """;
    @SneakyThrows
    public AiServiceImpl() {
        reloadApi();
    }

    @Override
    @SneakyThrows
    public AiResult generateResponse(String message) {
        boolean stream = false;
        boolean think = false;

        LocalDate today = LocalDate.now();
        String currentDate = today.toString();

        String prompt = String.format(
                "Today's date: %s\n\nEmail to analyze:\n%s",
                currentDate, message
        );
        ArrayList<MessageRequest> messageRequests = new ArrayList<>(
                Arrays.asList(
                        new MessageRequest("user", promptContext),
                        new MessageRequest("user", prompt)
                )
        );

        AiHistoryRequest aiHistoryRequest = new AiHistoryRequest(model, stream, messageRequests, think);
        Response<AIResponseHistory> response = ollamaApi.aiChat(aiHistoryRequest).execute();
        if (response.code() == 401 || response.code() == 403) {
            throw new InvalidApiKeyException();
        }
        if (!response.isSuccessful()) {
            throw new RuntimeException("Ошибка Ollama: " + response.code());
        }
        if (response.body() == null) {
            throw new RuntimeException("Пустой ответ от Ollama");
        }

        String email = response.body().getMessage().getContent();
        return parseResponse(email);
    }

    @SneakyThrows
    private AiResult parseResponse(String email) {
        email = cleanJson(email);
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(email, AiResult.class);
    }
    public void reloadApi() {
        this.ollamaApi = ClientManager.createClient(
                OllamaApi.class,
                ApiKeyManager.loadApiKey()
        );
    }
    @SneakyThrows
    private String cleanJson(String email) {
        return email
                .replaceAll("```", "")
                .replaceAll("json", "");
    }
}
