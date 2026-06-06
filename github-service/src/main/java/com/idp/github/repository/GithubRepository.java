package com.idp.github.repository;

import com.idp.github.entity.RepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubRepository
        extends JpaRepository<RepositoryEntity, Long> {
}