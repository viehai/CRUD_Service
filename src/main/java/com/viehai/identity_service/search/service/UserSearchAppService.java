package com.viehai.identity_service.search.service;

import com.viehai.identity_service.entity.User;
import com.viehai.identity_service.repository.UserRepository;
import com.viehai.identity_service.search.dto.AggregationResult;
import com.viehai.identity_service.search.dto.SearchResult;
import com.viehai.identity_service.search.model.UserDoc;
import com.viehai.identity_service.search.repo.UserDocRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSearchAppService {
    UserDocRepository docRepo;    // truy vấn Elasticsearch (repo)
    ElasticsearchOperations elasticsearchOperations;
    UserRepository userRepo;      // đọc MySQL cho reindex
    UserSearchSync sync;          // đồng bộ MySQL -> Elasticsearch

    @Transactional(readOnly = true)
    public List<UserDoc> searchFullText(String q) {
        if (q == null || q.isBlank()) return List.of();
        
        // Escape special characters in query string
        String escapedQ = escapeJsonString(q);
        
        // Build multi-match query using JSON string
        String queryJson = String.format("""
            {
              "multi_match": {
                "query": "%s",
                "fields": ["firstName", "lastName", "line", "ward", "city", "jobNames"],
                "type": "best_fields",
                "fuzziness": "AUTO"
              }
            }
            """, escapedQ);
        
        StringQuery query = new StringQuery(queryJson);
        return elasticsearchOperations.search(query, UserDoc.class)
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public SearchResult searchAdvanced(String q, String country, String city, Integer from, Integer size) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("{");
        queryBuilder.append("\"bool\": {");

        boolean hasFieldInBool = false; // để biết có cần thêm dấu phẩy hay không

        // MUST (multi_match)
        if (StringUtils.hasText(q)) {
            String escapedQ = escapeJsonString(q);
            queryBuilder.append("\"must\": [{");
            queryBuilder.append("\"multi_match\": {");
            queryBuilder.append("\"query\": \"").append(escapedQ).append("\",");
            queryBuilder.append("\"fields\": [");
            queryBuilder.append("\"firstName^2.0\",");
            queryBuilder.append("\"lastName^2.0\",");
            queryBuilder.append("\"city^1.5\",");
            queryBuilder.append("\"jobNames^1.5\",");
            queryBuilder.append("\"line\",");
            queryBuilder.append("\"ward\"");
            queryBuilder.append("],");
            queryBuilder.append("\"type\": \"best_fields\",");
            queryBuilder.append("\"fuzziness\": \"AUTO\"");
            queryBuilder.append("}");
            queryBuilder.append("}]");
            hasFieldInBool = true;
        }

        // FILTER (country, city)
        if (StringUtils.hasText(country) || StringUtils.hasText(city)) {
            if (hasFieldInBool) {
                queryBuilder.append(",");
            }
            queryBuilder.append("\"filter\": [");

            boolean hasFilter = false;
            if (StringUtils.hasText(country)) {
                String escapedCountry = escapeJsonString(country);
                queryBuilder.append("{\"match\": {\"country\": \"")
                        .append(escapedCountry).append("\"}}");
                hasFilter = true;
            }

            if (StringUtils.hasText(city)) {
                String escapedCity = escapeJsonString(city);
                if (hasFilter) {
                    queryBuilder.append(",");
                }
                // city: dùng match (vì là Text, có analyzer)
                queryBuilder.append("{\"match\": {\"city\": \"")
                        .append(escapedCity).append("\"}}");
            }

            queryBuilder.append("]");
        }

        queryBuilder.append("}"); // end bool
        queryBuilder.append("}"); // end root

        String queryJson = queryBuilder.toString();
        System.out.println("ES ADVANCED QUERY = " + queryJson);

        StringQuery query = new StringQuery(queryJson);

        Pageable pageable = PageRequest.of(
                from != null ? from : 0,
                size != null ? size : 20
        );
        query.setPageable(pageable);

        SearchHits<UserDoc> searchHits =
                elasticsearchOperations.search(query, UserDoc.class);

        Map<String, AggregationResult> aggregations = new HashMap<>();
        if (!StringUtils.hasText(country) && !StringUtils.hasText(city)) {
            aggregations.put("countries", aggregateByCountry());
            aggregations.put("cities", aggregateByCity());
            aggregations.put("jobCodes", aggregateByJobCodes());
        }

        return SearchResult.builder()
                .documents(searchHits.getSearchHits().stream()
                        .map(SearchHit::getContent)
                        .toList())
                .totalHits(searchHits.getTotalHits())
                .aggregations(aggregations)
                .build();
    }



    @Transactional(readOnly = true)
    public List<UserDoc> searchWithFilters(String firstName, String lastName, String country, 
                                           String city, List<String> jobCodes) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("{\"bool\": {");
        
        boolean hasMust = false;
        boolean hasFilter = false;
        
        // Build must clauses
        if (StringUtils.hasText(firstName) || StringUtils.hasText(lastName)) {
            queryBuilder.append("\"must\": [");
            if (StringUtils.hasText(firstName)) {
                String escapedFirstName = escapeJsonString(firstName);
                queryBuilder.append("{\"match\": {\"firstName\": \"").append(escapedFirstName).append("\"}}");
                hasMust = true;
            }
            if (StringUtils.hasText(lastName)) {
                if (hasMust) queryBuilder.append(",");
                String escapedLastName = escapeJsonString(lastName);
                queryBuilder.append("{\"match\": {\"lastName\": \"").append(escapedLastName).append("\"}}");
                hasMust = true;
            }
            queryBuilder.append("],");
        }
        
        // Build filter clauses
        if (StringUtils.hasText(country) || StringUtils.hasText(city) || (jobCodes != null && !jobCodes.isEmpty())) {
            queryBuilder.append("\"filter\": [");
            
            if (StringUtils.hasText(country)) {
                String escapedCountry = escapeJsonString(country);
                queryBuilder.append("{\"term\": {\"country\": \"").append(escapedCountry).append("\"}}");
                hasFilter = true;
            }
            
            if (StringUtils.hasText(city)) {
                if (hasFilter) queryBuilder.append(",");
                String escapedCity = escapeJsonString(city);
                queryBuilder.append("{\"term\": {\"city\": \"").append(escapedCity).append("\"}}");
                hasFilter = true;
            }
            
            if (jobCodes != null && !jobCodes.isEmpty()) {
                if (hasFilter) queryBuilder.append(",");
                queryBuilder.append("{\"terms\": {\"jobCodes\": [");
                for (int i = 0; i < jobCodes.size(); i++) {
                    if (i > 0) queryBuilder.append(",");
                    String escapedJobCode = escapeJsonString(jobCodes.get(i));
                    queryBuilder.append("\"").append(escapedJobCode).append("\"");
                }
                queryBuilder.append("]}}");
            }
            
            queryBuilder.append("]");
        }
        
        queryBuilder.append("}}");
        
        String queryJson = queryBuilder.toString();
        StringQuery query = new StringQuery(queryJson);
        
        return elasticsearchOperations.search(query, UserDoc.class)
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
    

    private String escapeJsonString(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }


    @Transactional(readOnly = true)
    public AggregationResult aggregateByCountry() {
        Iterable<UserDoc> allDocsIterable = docRepo.findAll();
        List<UserDoc> allDocs = StreamSupport.stream(allDocsIterable.spliterator(), false)
                .collect(Collectors.toList());
        
        Map<String, Long> buckets = allDocs.stream()
                .filter(doc -> doc.getCountry() != null)
                .collect(Collectors.groupingBy(
                        UserDoc::getCountry,
                        Collectors.counting()
                ));
        
        return AggregationResult.builder()
                .name("countries")
                .buckets(buckets)
                .uniqueCount((long) buckets.size())
                .totalCount(buckets.values().stream().mapToLong(Long::longValue).sum())
                .build();
    }


    @Transactional(readOnly = true)
    public AggregationResult aggregateByCity() {
        Iterable<UserDoc> allDocsIterable = docRepo.findAll();
        List<UserDoc> allDocs = StreamSupport.stream(allDocsIterable.spliterator(), false)
                .collect(Collectors.toList());
        
        Map<String, Long> buckets = allDocs.stream()
                .filter(doc -> doc.getCity() != null)
                .collect(Collectors.groupingBy(
                        UserDoc::getCity,
                        Collectors.counting()
                ));
        
        return AggregationResult.builder()
                .name("cities")
                .buckets(buckets)
                .uniqueCount((long) buckets.size())
                .totalCount(buckets.values().stream().mapToLong(Long::longValue).sum())
                .build();
    }


    @Transactional(readOnly = true)
    public AggregationResult aggregateByJobCodes() {
        Iterable<UserDoc> allDocsIterable = docRepo.findAll();
        List<UserDoc> allDocs = StreamSupport.stream(allDocsIterable.spliterator(), false)
                .collect(Collectors.toList());
        
        Map<String, Long> buckets = new HashMap<>();
        
        for (UserDoc doc : allDocs) {
            if (doc.getJobCodes() != null) {
                for (String jobCode : doc.getJobCodes()) {
                    buckets.put(jobCode, buckets.getOrDefault(jobCode, 0L) + 1);
                }
            }
        }
        
        return AggregationResult.builder()
                .name("jobCodes")
                .buckets(buckets)
                .uniqueCount((long) buckets.size())
                .totalCount(buckets.values().stream().mapToLong(Long::longValue).sum())
                .build();
    }


    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total count
        long totalCount = docRepo.count();
        stats.put("totalUsers", totalCount);

        // Unique countries
        AggregationResult countries = aggregateByCountry();
        stats.put("totalCountries", countries.getUniqueCount());
        stats.put("countries", countries.getBuckets());

        // Unique cities
        AggregationResult cities = aggregateByCity();
        stats.put("totalCities", cities.getUniqueCount());
        stats.put("cities", cities.getBuckets());

        // Job codes
        AggregationResult jobCodes = aggregateByJobCodes();
        stats.put("totalJobCodes", jobCodes.getUniqueCount());
        stats.put("jobCodes", jobCodes.getBuckets());

        return stats;
    }


    @Transactional(readOnly = true)
    public List<UserDoc> searchByUsername(String username) {
        if (username == null || username.isBlank()) return List.of();
        return docRepo.findByUsername(username);
    }

    @Transactional
    public int reindexAll() {
        List<User> all = userRepo.findAll();
        sync.upsertAll(all);
        return all.size();
    }
}