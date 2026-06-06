package com.idp.deployment.dto;

public record CreateDeploymentRequest(
        Long serviceId,
        String environment,
        String version,
        String image
) {
}