package com.idp.github.dto;

import java.time.LocalDateTime;

public record RepositoryResponse(

        Long id,

        String repoName,

        String repoUrl,

        String owner,

        String defaultBranch,

        LocalDateTime createdAt

) {
}