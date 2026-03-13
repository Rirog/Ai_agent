package org.example.api;

import org.example.models.request.AiHistoryRequest;
import org.example.models.request.AiRequest;
import org.example.models.response.AIResponse;
import org.example.models.response.AiHistoryResponse;
import org.example.models.test.AiCreateRequest;
import org.example.models.test.AiPushRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OllamaApi {

    @POST("api/generate")
    Call<AIResponse> aiGenerate(
            @Body AiRequest aiRequest
    );

    @POST("api/chat")
    Call<AiHistoryResponse> aiChat(
            @Body AiHistoryRequest aiHistoryRequest
    );

    @POST("create")
    Call<Void> aiCreate(
            @Body AiCreateRequest aiCreateRequest
    );

    @POST("push")
    Call<Void> aiPush(
            @Body AiPushRequest aiPushRequest
    );
}
