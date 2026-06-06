package com.idp.template.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GithubServiceClient {

    private final RestClient restClient;

    public String health() {

        try {

            return restClient.get()
                    .uri(
                            "http://github-service:8087/actuator/health"
                    )
                    .retrieve()
                    .body(String.class);

        } catch (Exception e) {

            return "github-service unavailable";
        }
    }

    public String createRepository(
            String repositoryName
    ) {

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