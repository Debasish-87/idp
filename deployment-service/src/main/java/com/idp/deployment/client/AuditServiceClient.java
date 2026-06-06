package com.idp.deployment.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class AuditServiceClient {

    private final RestTemplate restTemplate =
            new RestTemplate();

    public void createEvent(
            String eventType,
            String message
    ) {

        try {

            HttpHeaders headers =
                    new HttpHeaders();

            headers.setContentType(
                    MediaType.APPLICATION_JSON
            );

            Map<String, String> body =
                    Map.of(
                            "eventType", eventType,
                            "message", message
                    );

            HttpEntity<Map<String, String>> request =
                    new HttpEntity<>(
                            body,
                            headers
                    );

            restTemplate.postForObject(
                    "http://audit-service:8084/audit",
                    request,
                    Object.class
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}