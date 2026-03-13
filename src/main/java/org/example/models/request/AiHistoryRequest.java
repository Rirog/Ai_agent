package org.example.models.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiHistoryRequest {

    @JsonProperty("model")
    private String model;

    @JsonProperty("stream")
    private boolean stream;


    @JsonProperty("messages")
    private ArrayList<MessageRequest> history;

    @JsonProperty("think")
    private boolean think;
}
