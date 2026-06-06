package com.idp.deployment.service;

import com.idp.deployment.client.AuditServiceClient;
import com.idp.deployment.client.CatalogServiceClient;
import com.idp.deployment.client.KubernetesDeploymentClient;
import com.idp.deployment.dto.CreateDeploymentRequest;
import com.idp.deployment.dto.DeploymentResponse;
import com.idp.deployment.entity.Deployment;
import com.idp.deployment.repository.DeploymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeploymentService {

    private final DeploymentRepository repository;

    private final CatalogServiceClient catalogServiceClient;

    private final AuditServiceClient auditServiceClient;

    private final KubernetesDeploymentClient kubernetesDeploymentClient;

    public DeploymentResponse create(
            CreateDeploymentRequest request) {

        if (!catalogServiceClient.serviceExists(
                request.serviceId())) {

            throw new RuntimeException(
                    "Service not found"
            );
        }

        try {

            String deploymentName =
                    kubernetesDeploymentClient.deploy(
                            "service-" + request.serviceId(),
                            request.image(),
                            request.environment()
                    );

            Deployment deployment =
                    Deployment.builder()
                            .serviceId(
                                    request.serviceId()
                            )
                            .environment(
                                    request.environment()
                            )
                            .version(
                                    request.version()
                            )
                            .image(
                                    request.image()
                            )
                            .status(
                                    "DEPLOYED"
                            )
                            .createdAt(
                                    LocalDateTime.now()
                            )
                            .build();

            Deployment saved =
                    repository.save(
                            deployment
                    );

            auditServiceClient.createEvent(
                    "DEPLOYMENT_CREATED",
                    "Service "
                            + saved.getServiceId()
                            + " deployed to "
                            + saved.getEnvironment()
                            + " using deployment "
                            + deploymentName
            );

            return map(saved);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Kubernetes deployment failed",
                    e
            );
        }
    }

    public List<DeploymentResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    public DeploymentResponse getById(
            Long id) {

        Deployment deployment =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Deployment not found"
                                )
                        );

        return map(deployment);
    }

    public List<DeploymentResponse> getByServiceId(
            Long serviceId) {

        return repository
                .findByServiceId(serviceId)
                .stream()
                .map(this::map)
                .toList();
    }

    public List<DeploymentResponse> getByEnvironment(
            String environment) {

        return repository
                .findByEnvironment(environment)
                .stream()
                .map(this::map)
                .toList();
    }

    public DeploymentResponse rollback(
            Long deploymentId) {

        Deployment current =
                repository.findById(
                                deploymentId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Deployment not found"
                                )
                        );

        Deployment previous =
                repository
                        .findTopByServiceIdAndEnvironmentAndIdLessThanOrderByIdDesc(
                                current.getServiceId(),
                                current.getEnvironment(),
                                current.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No previous deployment found"
                                )
                        );

        kubernetesDeploymentClient.deploy(
                "service-" + previous.getServiceId(),
                previous.getImage(),
                previous.getEnvironment()
        );

        Deployment rollback =
                Deployment.builder()
                        .serviceId(
                                previous.getServiceId()
                        )
                        .environment(
                                previous.getEnvironment()
                        )
                        .version(
                                previous.getVersion()
                        )
                        .image(
                                previous.getImage()
                        )
                        .status(
                                "DEPLOYED"
                        )
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .build();

        Deployment saved =
                repository.save(
                        rollback
                );

        auditServiceClient.createEvent(
                "DEPLOYMENT_ROLLBACK",
                "Rollback from "
                        + current.getVersion()
                        + " to "
                        + previous.getVersion()
        );

        return map(saved);
    }

    public DeploymentResponse updateStatus(
            Long id,
            String status) {

        Deployment deployment =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Deployment not found"
                                )
                        );

        deployment.setStatus(status);

        Deployment saved =
                repository.save(
                        deployment
                );

        auditServiceClient.createEvent(
                "DEPLOYMENT_STATUS_CHANGED",
                "Deployment "
                        + saved.getId()
                        + " changed to "
                        + saved.getStatus()
        );

        return map(saved);
    }

    private DeploymentResponse map(
            Deployment deployment) {

        return new DeploymentResponse(
                deployment.getId(),
                deployment.getServiceId(),
                deployment.getEnvironment(),
                deployment.getVersion(),
                deployment.getImage(),
                deployment.getStatus(),
                deployment.getCreatedAt()
        );
    }
}
