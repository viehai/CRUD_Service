package com.viehai.identity_service.search.dto;

import com.viehai.identity_service.search.model.UserDoc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {
    private List<UserDoc> documents;
    private long totalHits;
    private Map<String, AggregationResult> aggregations;
}

