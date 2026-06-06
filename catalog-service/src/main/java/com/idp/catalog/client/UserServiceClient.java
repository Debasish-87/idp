package com.idp.catalog.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserServiceClient {

    private final RestClient restClient =
            RestClient.create();

    public boolean userExists(String username) {

        try {

            restClient.get()
                    .uri(
                            "http://user-service:8081/users/username/"
                                    + username
                    )
                    .retrieve()
                    .toEntity(String.class);

            return true;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }
}