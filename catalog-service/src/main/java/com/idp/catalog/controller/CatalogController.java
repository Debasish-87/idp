package com.idp.catalog.controller;

import com.idp.catalog.dto.CreateServiceRequest;
import com.idp.catalog.dto.ServiceResponse;
import com.idp.catalog.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @PostMapping
    public ServiceResponse create(
            @RequestBody CreateServiceRequest request) {

        return catalogService.create(request);
    }

    @GetMapping
    public List<ServiceResponse> getAll() {

        return catalogService.getAll();
    }

    @GetMapping("/{id}")
    public ServiceResponse getById(
            @PathVariable Long id) {

        return catalogService.getById(id);
    }
}