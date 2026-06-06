package com.idp.audit.repository;

import com.idp.audit.entity.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository
        extends JpaRepository<AuditEvent, Long> {
}