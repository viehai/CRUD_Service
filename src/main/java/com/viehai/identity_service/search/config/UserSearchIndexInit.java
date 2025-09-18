package com.viehai.identity_service.search.config;

import com.redis.om.spring.indexing.RediSearchIndexer;
import com.viehai.identity_service.search.model.UserDoc;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSearchIndexInit {
    RediSearchIndexer indexer;

    @EventListener(ApplicationReadyEvent.class)
    public void createIndexes() {
        log.info("Creating RediSearch index for UserDoc...");
        indexer.createIndexFor(UserDoc.class); // com.viehai_identity_service.search.model.UserDocIdx
        log.info("Index created (or already exists).");
    }
}