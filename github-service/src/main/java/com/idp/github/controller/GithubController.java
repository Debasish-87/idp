package com.idp.github.controller;

import com.idp.github.dto.CreateRepositoryRequest;
import com.idp.github.dto.RepositoryResponse;
import com.idp.github.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/github/repositories")
@RequiredArgsConstructor
public class GithubController {

    private final GithubService service;

    @PostMapping
    public RepositoryResponse create(
            @RequestBody
            CreateRepositoryRequest request
    ) {

        return service.create(request);
    }
}