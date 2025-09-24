package com.viehai.identity_service.bootstrap;

import com.viehai.identity_service.entity.Job;
import com.viehai.identity_service.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;  // hoặc: org.springframework.transaction.annotation.Transactional
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
/** chạy seeder khi active profile là 'dev' HOẶC 'mysql' */
@Profile({"dev","mysql"})
public class DevDataSeeder implements ApplicationRunner {

    JobRepository jobRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (jobRepository.count() == 0) {
            jobRepository.saveAll(List.of(
                    Job.builder().code("SE").name("Software Engineer").build(),
                    Job.builder().code("PM").name("Product Manager").build()
            ));
            log.info("Seeded jobs: SE, PM");
        } else {
            log.info("Jobs already present: {}", jobRepository.count());
        }
    }
}
