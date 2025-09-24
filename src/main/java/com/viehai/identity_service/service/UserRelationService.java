package com.viehai.identity_service.service;

import com.viehai.identity_service.dto.request.AddressRequest;
import com.viehai.identity_service.dto.response.AddressResponse;
import com.viehai.identity_service.dto.response.JobResponse;
import com.viehai.identity_service.entity.Address;
import com.viehai.identity_service.entity.Job;
import com.viehai.identity_service.entity.User;
import com.viehai.identity_service.repository.AddressRepository;
import com.viehai.identity_service.repository.JobRepository;
import com.viehai.identity_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserRelationService {

    UserRepository userRepo;
    JobRepository jobRepo;
    AddressRepository addressRepo;

    // Thay thế toàn bộ danh sách jobs của user
    @Transactional
    @CacheEvict(value = "users", key = "#userId", condition = "#userId != null")
    public void replaceJobs(String userId, List<Long> jobIds) {
        User u = userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        List<Job> jobs = jobRepo.findAllById(jobIds);
        if (jobs.size() != jobIds.size()) throw new EntityNotFoundException("Some jobs not found");
        u.setJobs(new LinkedHashSet<>(jobs));
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId", condition = "#userId != null")
    public void removeJob(String userId, Long jobId) {
        User u = userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        u.getJobs().removeIf(j -> j.getId().equals(jobId));
    }

    @Transactional
    @CacheEvict(value="users", key="#userId")
    public AddressResponse upsertAddress(String userId, AddressRequest req) {
        User u = userRepo.findWithRelationsById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Address a = (u.getAddress() == null) ? new Address() : u.getAddress();
        copy(a, req);
        u.setAddress(a);          // owner side
        userRepo.save(u);         // cascade sẽ persist/merge Address & update users.address_id
        return new AddressResponse(a.getId(), a.getLine1(), a.getCity(), a.getCountry());
    }

    @Transactional
    @CacheEvict(value="users", key="#userId")
    public void removeAddress(String userId){
        User u = userRepo.findWithRelationsById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        u.setAddress(null);       // orphanRemoval => Hibernate sẽ xoá record ở addresses
        userRepo.save(u);
    }



    @Transactional
    public List<JobResponse> getJobs(String userId) {
        User u = userRepo.findWithRelationsById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return u.getJobs().stream()
                .map(j -> new JobResponse(j.getId(), j.getCode(), j.getName()))
                .toList();
    }

    @Transactional
    public AddressResponse getAddress(String userId) {
        User u = userRepo.findWithRelationsById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Address a = u.getAddress();
        if (a == null) return null;
        AddressResponse res = new AddressResponse();
        res.setId(a.getId());
        res.setLine1(a.getLine1());

        res.setCity(a.getCity());
        res.setCountry(a.getCountry());

        return res;
    }

    // copy dto -> entity (đơn giản)
    private void copy(Address a, AddressRequest req) {
        a.setLine1(req.getLine1());
        a.setLine2(req.getLine2());
        a.setWard(req.getWard());
        a.setDistrict(req.getDistrict());
        a.setCity(req.getCity());
        a.setCountry(req.getCountry() == null ? "VN" : req.getCountry());
        a.setPostalCode(req.getPostalCode());
    }
}
