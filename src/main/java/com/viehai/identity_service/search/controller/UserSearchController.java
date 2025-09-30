package com.viehai.identity_service.search.controller;

import com.viehai.identity_service.search.model.UserDoc;
import com.viehai.identity_service.search.service.UserSearchAppService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSearchController {
    UserSearchAppService appService;

    @GetMapping("/fulltext")
    public List<UserDoc> fullText(@RequestParam String q) {
        return appService.searchFullText(q);
    }

    @GetMapping("/by-username")
    public List<UserDoc> byUsername(@RequestParam("u") String username) {
        return appService.searchByUsername(username);
    }

    @PostMapping("/reindex")
    public String reindexAll() {
        int n = appService.reindexAll();
        return "Reindexed: " + n;
    }
}