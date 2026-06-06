package com.idp.github.service;

import com.idp.github.client.GithubClient;
import com.idp.github.dto.CreateRepositoryRequest;
import com.idp.github.dto.RepositoryResponse;
import com.idp.github.entity.RepositoryEntity;
import com.idp.github.repository.GithubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubService {

    private final GithubRepository repository;

    private final GithubClient githubClient;

    public RepositoryResponse create(
            CreateRepositoryRequest request
    ) {

        try {

            String repoUrl =
                    githubClient.createRepository(
                            request.repoName()
                    );

            RepositoryEntity entity =
                    RepositoryEntity.builder()
                            .repoName(
                                    request.repoName()
                            )
                            .repoUrl(
                                    repoUrl
                            )
                            .owner(
                                    "github"
                            )
                            .defaultBranch(
                                    "main"
                            )
                            .createdAt(
                                    LocalDateTime.now()
                            )
                            .build();

            return map(
                    repository.save(entity)
            );

        } catch (
                HttpClientErrorException.UnprocessableEntity e
        ) {

            log.error(
                    "Repository already exists: {}",
                    request.repoName()
            );

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Repository already exists"
            );

        } catch (
                HttpClientErrorException.Forbidden e
        ) {

            log.error(
                    "GitHub permission denied",
                    e
            );

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "GitHub token does not have required permissions"
            );

        } catch (
                HttpClientErrorException.Unauthorized e
        ) {

            log.error(
                    "GitHub authentication failed",
                    e
            );

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid GitHub token"
            );

        } catch (Exception e) {

            log.error(
                    "GitHub repository creation failed",
                    e
            );

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create GitHub repository"
            );
        }
    }

    private RepositoryResponse map(
            RepositoryEntity entity
    ) {

        return new RepositoryResponse(
                entity.getId(),
                entity.getRepoName(),
                entity.getRepoUrl(),
                entity.getOwner(),
                entity.getDefaultBranch(),
                entity.getCreatedAt()
        );
    }
}