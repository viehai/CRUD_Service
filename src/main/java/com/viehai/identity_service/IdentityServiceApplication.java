package com.viehai.identity_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = "com.viehai.identity_service.repository")
@EnableElasticsearchRepositories(basePackages = "com.viehai.identity_service.search.repo")
public class IdentityServiceApplication {

	public static void main(String[] args) {

        SpringApplication.run(IdentityServiceApplication.class, args);
	}

}
