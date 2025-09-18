package com.viehai.identity_service.search.repo;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.viehai.identity_service.search.model.UserDoc;

import java.util.List;

public interface UserDocRepository extends RedisDocumentRepository<UserDoc, String> {
    List<UserDoc> findByUsername(String username);
    List<UserDoc> findByFirstNameStartingWithIgnoreCase(String prefix);
    List<UserDoc> findByLastNameContainingIgnoreCase(String infix);
}
