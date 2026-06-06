package com.idp.catalog.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class GithubServiceClient {

    private final RestClient restClient =
            RestClient.create();

    public String createRepository(
            String repositoryName) {

        Map<String, String> request =
                Map.of(
                        "repoName",
                        repositoryName
                );

        Map response =
                restClient.post()
                        .uri(
                                "http://github-service:8087/github/repositories"
                        )
                        .body(request)
                        .retrieve()
                        .body(Map.class);

        return (String)
                response.get("repoUrl");
    }
}