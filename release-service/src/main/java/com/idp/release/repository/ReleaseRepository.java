package com.idp.release.repository;

import com.idp.release.entity.ReleaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReleaseRepository
        extends JpaRepository<ReleaseEntity, Long> {
}
