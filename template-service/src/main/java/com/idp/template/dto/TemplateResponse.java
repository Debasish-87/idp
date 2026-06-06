package com.idp.template.dto;

public record TemplateResponse(

        String serviceName,

        String status,

        String path,

        String repositoryUrl

) {
}