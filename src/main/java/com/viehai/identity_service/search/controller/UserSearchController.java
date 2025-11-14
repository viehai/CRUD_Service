package com.viehai.identity_service.search.controller;

import com.viehai.identity_service.search.dto.AggregationResult;
import com.viehai.identity_service.search.dto.SearchResult;
import com.viehai.identity_service.search.model.UserDoc;
import com.viehai.identity_service.search.service.UserSearchAppService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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


    @GetMapping("/advanced")
    public SearchResult advancedSearch(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        return appService.searchAdvanced(q, country, city, from, size);
    }


    @GetMapping("/filter")
    public List<UserDoc> searchWithFilters(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) List<String> jobCodes) {
        return appService.searchWithFilters(firstName, lastName, country, city, jobCodes);
    }


    @GetMapping("/aggregations/country")
    public AggregationResult aggregateByCountry() {
        return appService.aggregateByCountry();
    }


    @GetMapping("/aggregations/city")
    public AggregationResult aggregateByCity() {
        return appService.aggregateByCity();
    }


    @GetMapping("/aggregations/job-codes")
    public AggregationResult aggregateByJobCodes() {
        return appService.aggregateByJobCodes();
    }


    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        return appService.getStatistics();
    }

    @PostMapping("/reindex")
    public String reindexAll() {
        int n = appService.reindexAll();
        return "Reindexed: " + n;
    }
}