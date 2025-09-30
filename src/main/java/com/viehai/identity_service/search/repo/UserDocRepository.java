package com.viehai.identity_service.search.repo;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.viehai.identity_service.search.model.UserDoc;

import java.util.List;

public interface UserDocRepository extends RedisDocumentRepository<UserDoc, String> {
    List<UserDoc> findByUsername(String username);                    // khớp chính xác
    List<UserDoc> findByFirstNameContainingIgnoreCase(String q);      // full-text đơn giản
    List<UserDoc> findByLastNameContainingIgnoreCase(String q);       // full-text đơn giản
}
