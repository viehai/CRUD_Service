package com.viehai.identity_service.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregationResult {
    private String name;
    private Map<String, Long> buckets;
    private Long totalCount;
    private Long uniqueCount;
}

