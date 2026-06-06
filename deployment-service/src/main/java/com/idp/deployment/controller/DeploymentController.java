package com.idp.deployment.controller;

import com.idp.deployment.dto.CreateDeploymentRequest;
import com.idp.deployment.dto.DeploymentResponse;
import com.idp.deployment.service.DeploymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deployments")
@RequiredArgsConstructor
public class DeploymentController {

    private final DeploymentService deploymentService;

    @PostMapping
    public DeploymentResponse create(
            @RequestBody CreateDeploymentRequest request) {

        return deploymentService.create(request);
    }

    @GetMapping
    public List<DeploymentResponse> getAll() {

        return deploymentService.getAll();
    }

    @GetMapping("/{id}")
    public DeploymentResponse getById(
            @PathVariable Long id) {

        return deploymentService.getById(id);
    }

    @GetMapping("/service/{serviceId}")
    public List<DeploymentResponse>
    getByServiceId(
            @PathVariable Long serviceId) {

        return deploymentService
                .getByServiceId(serviceId);
    }

    @GetMapping("/environment/{environment}")
    public List<DeploymentResponse>
    getByEnvironment(
            @PathVariable String environment) {

        return deploymentService
                .getByEnvironment(environment);
    }

    @PostMapping("/{id}/rollback")
    public DeploymentResponse rollback(
            @PathVariable Long id) {

        return deploymentService
                .rollback(id);
    }

    @PutMapping("/{id}/status")
    public DeploymentResponse updateStatus(
            @PathVariable Long id,
            @RequestBody String status) {

        return deploymentService.updateStatus(
                id,
                status.replace("\"", "")
        );
    }
}