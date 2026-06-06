package com.idp.audit.dto;

public record CreateAuditRequest(
        String eventType,
        String message
) {
}