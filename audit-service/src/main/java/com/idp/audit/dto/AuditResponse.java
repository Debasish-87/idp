package com.idp.audit.dto;

import java.time.LocalDateTime;

public record AuditResponse(
        Long id,
        String eventType,
        String message,
        LocalDateTime createdAt
) {
}