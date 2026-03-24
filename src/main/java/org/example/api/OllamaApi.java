package org.example.api;

import org.example.dto.request.AiRequest;
import org.example.dto.response.AIResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OllamaApi {

    @POST("api/generate")
    Call<AIResponse> aiGenerate(
            @Body AiRequest aiRequest
    );
}
