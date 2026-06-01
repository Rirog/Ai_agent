package org.example.api;

import org.example.dto.request.AiHistoryRequest;
import org.example.dto.response.AIResponseHistory;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OllamaApi {
    @POST("chat")
    Call<AIResponseHistory> aiChat(
            @Body AiHistoryRequest aiRequest
    );
}
