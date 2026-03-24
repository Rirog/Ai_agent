package org.example.service;

import org.example.dto.response.AiResult;

public interface AiService {

    AiResult generateResponse(String message);
}
