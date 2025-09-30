package com.viehai.identity_service.search.service;

import com.redis.om.spring.search.stream.EntityStream;
import com.viehai.identity_service.entity.User;
import com.viehai.identity_service.repository.UserRepository;
import com.viehai.identity_service.search.model.UserDoc;
import com.viehai.identity_service.search.repo.UserDocRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSearchAppService {
    EntityStream stream;          // full-text
    UserDocRepository docRepo;    // truy vấn RediSearch (repo)
    UserRepository userRepo;      // đọc MySQL cho reindex
    UserSearchSync sync;          // đồng bộ MySQL -> Redis

    @Transactional(readOnly = true)
    public List<UserDoc> searchFullText(String q) {
        if (q == null || q.isBlank()) return List.of();
        return stream.of(UserDoc.class).filter(q).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDoc> searchByUsername(String username) {
        if (username == null || username.isBlank()) return List.of();
        return docRepo.findByUsername(username);
    }

    @Transactional
    public int reindexAll() {
        List<User> all = userRepo.findAll();
        sync.upsertAll(all);
        return all.size();
    }
}