package com.viehai.identity_service.controller;

import com.viehai.identity_service.dto.response.ApiResponse;
import com.viehai.identity_service.dto.request.JobCreateRequest;
import com.viehai.identity_service.dto.response.JobResponse;
import com.viehai.identity_service.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> create(@RequestBody JobCreateRequest req) {
        var data = jobService.create(req);
        var res = new ApiResponse<JobResponse>();
        res.setResult(data);
        return ResponseEntity.ok(res);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobResponse>>> list() {
        var data = jobService.findAll();
        var res = new ApiResponse<List<JobResponse>>();
        res.setResult(data);
        return ResponseEntity.ok(res);
    }
}
