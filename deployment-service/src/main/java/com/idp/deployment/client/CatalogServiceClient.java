package com.idp.deployment.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class CatalogServiceClient {

    private final RestClient restClient =
            RestClient.create();

    public boolean serviceExists(
            Long serviceId
    ) {

        try {

            restClient.get()
                    .uri(
                            "http://catalog-service:8082/services/"
                                    + serviceId
                    )
                    .retrieve()
                    .toEntity(String.class);

            return true;

        } catch (Exception e) {

            log.error(
                    "Catalog service lookup failed for serviceId={}",
                    serviceId,
                    e
            );

            return false;
        }
    }
}