package com.viehai.identity_service.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.viehai.identity_service.entity.User;

import java.util.List;
import java.util.Optional;

@Profile({"mysql", "postgres"})  // chỉ bật khi chạy jpa
public interface UserRepository extends JpaRepository<User, String> {

    @EntityGraph(attributePaths = {"jobs", "address"})
    Optional<User> findWithRelationsById(String id);


    Optional<User> findByUsername(String name);
    boolean existsByUsername(String username);
    List<User> findAllByJobs_Id(Long jobId);
}

