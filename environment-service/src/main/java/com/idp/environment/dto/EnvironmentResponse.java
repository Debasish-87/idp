package com.idp.environment.dto;

import java.time.LocalDateTime;

public record EnvironmentResponse(

        Long id,

        String name,

        String cluster,

        String namespace,

        String region,

        Boolean active,

        LocalDateTime createdAt
) {
}