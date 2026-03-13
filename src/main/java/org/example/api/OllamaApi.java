package org.example.api;

import org.example.models.AIResponse;
import org.example.models.AiRequest;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OllamaService {

    @POST("generate")
    Response<AIResponse> request(
            @Body AiRequest aiRequest
    );

}
