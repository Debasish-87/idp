package com.idp.template.dto;

public record CreateTemplateRequest(

        String serviceName,

        String groupId,

        String artifactId,

        String database,

        String packageName

) {
}