package com.idp.catalog.service;

import com.idp.catalog.client.GithubServiceClient;
import com.idp.catalog.client.UserServiceClient;
import com.idp.catalog.dto.CreateServiceRequest;
import com.idp.catalog.dto.ServiceResponse;
import com.idp.catalog.entity.ServiceEntity;
import com.idp.catalog.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final ServiceRepository repository;

    private final UserServiceClient userServiceClient;

    private final GithubServiceClient githubServiceClient;

    public ServiceResponse create(
            CreateServiceRequest request) {

        if (!userServiceClient.userExists(
                request.owner())) {

            throw new RuntimeException(
                    "Owner does not exist"
            );
        }

        String repositoryName =
                request.name()
                        .toLowerCase()
                        .replace(" ", "-");

        String repositoryUrl;

        try {

            repositoryUrl =
                    githubServiceClient
                            .createRepository(
                                    repositoryName
                            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to create GitHub repository"
            );
        }

        ServiceEntity entity =
                ServiceEntity.builder()
                        .name(
                                request.name()
                        )
                        .owner(
                                request.owner()
                        )
                        .description(
                                request.description()
                        )
                        .repositoryName(
                                repositoryName
                        )
                        .repositoryUrl(
                                repositoryUrl
                        )
                        .gitProvider(
                                "github"
                        )
                        .build();

        return map(
                repository.save(entity)
        );
    }

    public List<ServiceResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    public ServiceResponse getById(
            Long id) {

        ServiceEntity service =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Service not found"
                                ));

        return map(service);
    }

    private ServiceResponse map(
            ServiceEntity service) {

        return new ServiceResponse(
                service.getId(),
                service.getName(),
                service.getOwner(),
                service.getDescription(),
                service.getRepositoryName(),
                service.getRepositoryUrl(),
                service.getGitProvider()
        );
    }
}