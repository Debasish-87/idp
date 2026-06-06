package com.idp.release.service;

import com.idp.release.dto.CreateReleaseRequest;
import com.idp.release.dto.ReleaseResponse;
import com.idp.release.entity.ReleaseEntity;
import com.idp.release.repository.ReleaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReleaseService {

    private final ReleaseRepository repository;

    public ReleaseResponse create(
            CreateReleaseRequest request) {

        ReleaseEntity release =
                ReleaseEntity.builder()
                        .serviceId(request.serviceId())
                        .deploymentId(request.deploymentId())
                        .version(request.version())
                        .environment(request.environment())

                        // FIX
                        .status("CREATED")

                        .createdBy(request.createdBy())
                        .createdAt(LocalDateTime.now())
                        .build();

        return map(
                repository.save(release)
        );
    }

    public List<ReleaseResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    public ReleaseResponse getById(
            Long id) {

        ReleaseEntity release =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Release not found"
                                ));

        return map(release);
    }

    private ReleaseResponse map(
            ReleaseEntity release) {

        return new ReleaseResponse(
                release.getId(),
                release.getServiceId(),
                release.getDeploymentId(),
                release.getVersion(),
                release.getEnvironment(),
                release.getStatus(),
                release.getCreatedBy(),
                release.getCreatedAt()
        );
    }
}