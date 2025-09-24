package com.viehai.identity_service.service;

import com.viehai.identity_service.dto.request.JobCreateRequest;
import com.viehai.identity_service.dto.response.JobResponse;
import com.viehai.identity_service.entity.Job;
import com.viehai.identity_service.exception.AppException;
import com.viehai.identity_service.exception.ErrorCode;
import com.viehai.identity_service.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobService {

    JobRepository jobRepository;

    @Transactional
    public JobResponse create(JobCreateRequest req) {
        if (jobRepository.existsByCode(req.getCode())) throw new AppException(ErrorCode.JOB_CODE_EXISTS);

        Job job = Job.builder().code(req.getCode()).name(req.getName()).build();
        job = jobRepository.save(job);
        return JobResponse.builder()
                .id(job.getId()).code(job.getCode()).name(job.getName())
                .build();
    }

    @Transactional(readOnly = true)
    public List<JobResponse> findAll() {
        return jobRepository.findAll().stream()
                .map(j -> JobResponse.builder()
                        .id(j.getId()).code(j.getCode()).name(j.getName())
                        .build())
                .toList();
    }
}
