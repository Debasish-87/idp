package com.idp.release.dto;

import java.time.LocalDateTime;

public record ReleaseResponse(

Long id,

Long serviceId,

Long deploymentId,

String version,

String environment,

String status,

String createdBy,

LocalDateTime createdAt

) {
}
