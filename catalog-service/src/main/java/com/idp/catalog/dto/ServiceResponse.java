package com.idp.catalog.dto;

public record ServiceResponse(

        Long id,

        String name,

        String owner,

        String description,

        String repositoryName,

        String repositoryUrl,

        String gitProvider

) {
}