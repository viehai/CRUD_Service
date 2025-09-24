package com.viehai.identity_service.repository;

import com.viehai.identity_service.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
    boolean existsByCode(String code);
}
