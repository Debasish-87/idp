package com.idp.template.service;

import com.idp.template.client.GithubServiceClient;
import com.idp.template.dto.CreateTemplateRequest;
import com.idp.template.dto.TemplateResponse;
import com.idp.template.generator.ProjectGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final ProjectGenerator generator;

    private final GithubServiceClient githubServiceClient;

    private final GitPushService gitPushService;

    public TemplateResponse create(
            CreateTemplateRequest request
    ) {

        generator.generate(request);

        String repositoryUrl =
                githubServiceClient.createRepository(
                        request.serviceName()
                );

        gitPushService.push(
                request.serviceName()
        );

        return new TemplateResponse(
                request.serviceName(),
                "GENERATED",
                "generated/" + request.serviceName(),
                repositoryUrl
        );
    }
}