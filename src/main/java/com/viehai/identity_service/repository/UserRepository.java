package com.viehai.identity_service.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import com.viehai.identity_service.entity.User;

import java.util.Optional;

@Profile({"mysql", "postgres"})  // chỉ bật khi chạy jpa
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String name);
    boolean existsByUsername(String username);
}

