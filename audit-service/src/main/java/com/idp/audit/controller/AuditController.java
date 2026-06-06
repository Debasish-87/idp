package com.idp.audit.controller;

import com.idp.audit.dto.AuditResponse;
import com.idp.audit.dto.CreateAuditRequest;
import com.idp.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @PostMapping
    public AuditResponse create(
            @RequestBody CreateAuditRequest request) {

        return auditService.create(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER')")
    @GetMapping
    public List<AuditResponse> getAll() {

        return auditService.getAll();
    }
}