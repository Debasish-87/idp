package com.idp.release.dto;

public record CreateReleaseRequest(

Long serviceId,

Long deploymentId,

String version,

String environment,

String status,

String createdBy

) {
}
