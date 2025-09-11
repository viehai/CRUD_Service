package com.viehai.identity_service.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import com.viehai.identity_service.entity.User;

@Profile({"mysql", "postgres"})  // chỉ bật khi chạy jpa
public interface UserRepository extends JpaRepository<User, String> {


    boolean existsByUsername(String username);
}

