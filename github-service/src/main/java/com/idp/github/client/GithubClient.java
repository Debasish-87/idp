package com.idp.github.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class GithubClient {

    private final RestClient restClient;

    @Value("${github.token}")
    private String token;

    public GithubClient() {

        this.restClient =
                RestClient.builder()
                        .defaultHeader(
                                HttpHeaders.ACCEPT,
                                "application/vnd.github+json"
                        )
                        .build();
    }

    public String createRepository(
            String repositoryName
    ) {

        Map<String, Object> request =
                Map.of(
                        "name", repositoryName,
                        "private", true
                );

        Map response =
                restClient.post()
                        .uri(
                                "https://api.github.com/user/repos"
                        )
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + token
                        )
                        .body(request)
                        .retrieve()
                        .body(Map.class);

        return (String)
                response.get("html_url");
    }
}