package org.example.models.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiCreateRequest {

    @JsonProperty("from")
    private String from;

    @JsonProperty("model")
    private String model;

    @JsonProperty("system")
    private String prompt;
}
