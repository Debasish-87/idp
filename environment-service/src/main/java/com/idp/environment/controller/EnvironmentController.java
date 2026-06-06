package com.idp.environment.controller;

import com.idp.environment.dto.CreateEnvironmentRequest;
import com.idp.environment.dto.EnvironmentResponse;
import com.idp.environment.service.EnvironmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/environments")
@RequiredArgsConstructor
public class EnvironmentController {

    private final EnvironmentService service;

    @PostMapping
    public EnvironmentResponse create(
            @RequestBody
            CreateEnvironmentRequest request) {

        return service.create(request);
    }

    @GetMapping
    public List<EnvironmentResponse> getAll() {

        return service.getAll();
    }

    @GetMapping("/{id}")
    public EnvironmentResponse getById(
            @PathVariable Long id) {

        return service.getById(id);
    }
}