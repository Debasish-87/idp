package com.idp.release.controller;

import com.idp.release.dto.CreateReleaseRequest;
import com.idp.release.dto.ReleaseResponse;
import com.idp.release.service.ReleaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/releases")
@RequiredArgsConstructor
public class ReleaseController {

    private final ReleaseService service;

    @PostMapping
    public ReleaseResponse create(
            @RequestBody
            CreateReleaseRequest request) {

        return service.create(request);
    }

    @GetMapping
    public List<ReleaseResponse> getAll() {

        return service.getAll();
    }

    @GetMapping("/{id}")
    public ReleaseResponse getById(
            @PathVariable Long id) {

        return service.getById(id);
    }

}
