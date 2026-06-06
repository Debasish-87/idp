package com.idp.template.generator;

import com.idp.template.dto.CreateTemplateRequest;

public interface ProjectGenerator {

    void generate(
            CreateTemplateRequest request
    );
}