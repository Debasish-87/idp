package com.idp.audit.service;

import com.idp.audit.dto.AuditResponse;
import com.idp.audit.dto.CreateAuditRequest;
import com.idp.audit.entity.AuditEvent;
import com.idp.audit.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository repository;

    public AuditResponse create(
            CreateAuditRequest request) {

        AuditEvent event =
                AuditEvent.builder()
                        .eventType(request.eventType())
                        .message(request.message())
                        .createdAt(LocalDateTime.now())
                        .build();

        return map(
                repository.save(event)
        );
    }

    public List<AuditResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    private AuditResponse map(
            AuditEvent event) {

        return new AuditResponse(
                event.getId(),
                event.getEventType(),
                event.getMessage(),
                event.getCreatedAt()
        );
    }
}