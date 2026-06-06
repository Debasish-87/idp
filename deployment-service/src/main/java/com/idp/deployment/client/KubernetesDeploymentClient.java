package com.idp.deployment.client;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KubernetesDeploymentClient {

    private final KubernetesClient kubernetesClient;

    public String deploy(
            String serviceName,
            String image,
            String namespace
    ) {

        try {

            if (serviceName == null ||
                    serviceName.isBlank()) {

                throw new IllegalArgumentException(
                        "Service name cannot be empty"
                );
            }

            if (image == null ||
                    image.isBlank()) {

                throw new IllegalArgumentException(
                        "Image cannot be empty"
                );
            }

            if (namespace == null ||
                    namespace.isBlank()) {

                namespace = "default";
            }

            log.info(
                    "Ensuring namespace exists: {}",
                    namespace
            );

            kubernetesClient.namespaces()
                    .resource(
                            new NamespaceBuilder()
                                    .withNewMetadata()
                                    .withName(namespace)
                                    .endMetadata()
                                    .build()
                    )
                    .createOrReplace();

            Container container =
                    new ContainerBuilder()
                            .withName(serviceName)
                            .withImage(image)
                            .withImagePullPolicy("Always")
                            .addNewPort()
                            .withContainerPort(8080)
                            .endPort()
                            .build();

            Deployment deployment =
                    new DeploymentBuilder()

                            .withNewMetadata()
                            .withName(serviceName)
                            .withNamespace(namespace)
                            .endMetadata()

                            .withNewSpec()
                            .withReplicas(1)

                            .withNewSelector()
                            .addToMatchLabels(
                                    "app",
                                    serviceName
                            )
                            .endSelector()

                            .withNewTemplate()

                            .withNewMetadata()
                            .addToLabels(
                                    "app",
                                    serviceName
                            )
                            .addToLabels(
                                    "version",
                                    image.replace(":", "-")
                            )
                            .endMetadata()

                            .withNewSpec()
                            .withContainers(container)
                            .endSpec()

                            .endTemplate()

                            .endSpec()

                            .build();

            log.info(
                    "Deploying service={} image={} namespace={}",
                    serviceName,
                    image,
                    namespace
            );

            kubernetesClient.apps()
                    .deployments()
                    .inNamespace(namespace)
                    .resource(deployment)
                    .createOrReplace();

            log.info(
                    "Deployment created successfully: {}",
                    serviceName
            );

            return serviceName;

        } catch (Exception e) {

            log.error(
                    "Kubernetes deployment failed for service={}",
                    serviceName,
                    e
            );

            throw new RuntimeException(
                    "Failed to deploy service "
                            + serviceName,
                    e
            );
        }
    }
}