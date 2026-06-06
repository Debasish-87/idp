package com.idp.environment.repository;

import com.idp.environment.entity.EnvironmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvironmentRepository
        extends JpaRepository<
        EnvironmentEntity,
        Long> {
}