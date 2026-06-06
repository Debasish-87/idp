package com.idp.template.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class CatalogServiceClient {

    private final RestClient restClient;

    public String health() {

        try {

            return restClient.get()
                    .uri(
                            "http://catalog-service:8082/health"
                    )
                    .retrieve()
                    .body(String.class);

        } catch (Exception e) {

            return "catalog-service unavailable";
        }
    }
}