package com.viehai.identity_service.search.service;

import com.viehai.identity_service.entity.User;
import com.viehai.identity_service.search.model.UserDoc;
import com.viehai.identity_service.search.repo.UserDocRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSearchSync {
    UserDocRepository repo;

    public void upsertFrom(User u) {
        if (u == null) return;
        repo.save(toDoc(u));
    }

    public void upsertAll(List<User> users) {
        if (users == null || users.isEmpty()) return;
        repo.saveAll(users.stream().filter(Objects::nonNull).map(this::toDoc).toList());
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    private UserDoc toDoc(User u) {
        return UserDoc.builder()
                .id(u.getId())
                .username(u.getUsername())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .build();
    }
}
