package org.example.startTest;

import lombok.SneakyThrows;
import org.example.api.OllamaApi;
import org.example.client.ClientManager;
import org.example.config.ConfigManager;
import org.example.models.test.AiCreateRequest;
import retrofit2.Response;

public class AiCreate {
    private final OllamaApi ollamaApi = ClientManager.createClient(OllamaApi.class);

    @SneakyThrows
    public void createAi() {
        String prompt = """
                Ты должен отвечать лишь номерами команд из списка ничего больше
                если их больше чем одна команда просто отвечай списоком номеров,
                вот список команд:
                1 - показать список контейнеров
                2 - создать новый конйтенр
                3 - удалить контйенр
                4 - остановить контейнер
                5 - запустить контейнер
                """;
        AiCreateRequest aiCreateRequest = new AiCreateRequest(ConfigManager.getAiModelFast(), ConfigManager.getAiModel(), prompt);

        ollamaApi.aiCreate(aiCreateRequest).execute();
    }
}
