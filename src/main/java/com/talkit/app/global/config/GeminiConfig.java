package com.talkit.app.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GeminiConfig {

    // yml 설정 경로와 맞춤: custom.gemini.key
    @Value("${custom.gemini.key}")
    private String geminiApiKey;

    @Bean
    public RestTemplate geminiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("x-goog-api-key", geminiApiKey);
            return execution.execute(request, body);
        });

        return restTemplate;
    }
}
