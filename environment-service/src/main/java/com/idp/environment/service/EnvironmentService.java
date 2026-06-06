package com.idp.environment.service;

import com.idp.environment.dto.CreateEnvironmentRequest;
import com.idp.environment.dto.EnvironmentResponse;
import com.idp.environment.entity.EnvironmentEntity;
import com.idp.environment.repository.EnvironmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnvironmentService {

    private final EnvironmentRepository repository;

    public EnvironmentResponse create(
            CreateEnvironmentRequest request) {

        EnvironmentEntity environment =
                EnvironmentEntity.builder()
                        .name(request.name())
                        .cluster(request.cluster())
                        .namespace(request.namespace())
                        .region(request.region())
                        .active(true)
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .build();

        return map(
                repository.save(environment)
        );
    }

    public List<EnvironmentResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    public EnvironmentResponse getById(
            Long id) {

        EnvironmentEntity environment =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Environment not found"
                                ));

        return map(environment);
    }

    private EnvironmentResponse map(
            EnvironmentEntity environment) {

        return new EnvironmentResponse(
                environment.getId(),
                environment.getName(),
                environment.getCluster(),
                environment.getNamespace(),
                environment.getRegion(),
                environment.getActive(),
                environment.getCreatedAt()
        );
    }
}