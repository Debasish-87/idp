package com.idp.deployment.repository;

import com.idp.deployment.entity.Deployment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeploymentRepository
        extends JpaRepository<Deployment, Long> {

    List<Deployment> findByServiceId(
            Long serviceId
    );

    List<Deployment> findByEnvironment(
            String environment
    );

    Optional<Deployment>
    findTopByServiceIdAndEnvironmentAndIdLessThanOrderByIdDesc(
            Long serviceId,
            String environment,
            Long id
    );
}