package com.idp.template.controller;

import com.idp.template.dto.CreateTemplateRequest;
import com.idp.template.dto.TemplateResponse;
import com.idp.template.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService service;

    @PostMapping("/java-service")
    public TemplateResponse create(
            @RequestBody
            CreateTemplateRequest request
    ) {

        return service.create(request);
    }
}