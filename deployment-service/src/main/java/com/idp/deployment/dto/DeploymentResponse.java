package com.idp.deployment.dto;

import java.time.LocalDateTime;

public record DeploymentResponse(

        Long id,

        Long serviceId,

        String environment,

        String version,

        String image,

        String status,

        LocalDateTime createdAt

) {
}