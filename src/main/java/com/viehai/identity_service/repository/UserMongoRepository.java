package com.viehai.identity_service.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.viehai.identity_service.entity.UserMongo;

@Profile("mongodb")  // chỉ bật khi chạy mongo
public interface UserMongoRepository extends MongoRepository<UserMongo, String> {
}

