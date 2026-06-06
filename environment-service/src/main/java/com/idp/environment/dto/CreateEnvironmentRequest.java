package com.idp.environment.dto;

public record CreateEnvironmentRequest(

        String name,

        String cluster,

        String namespace,

        String region
) {
}