package com.viehai.identity_service.service;

import com.viehai.identity_service.dto.request.UserCreateRequest;
import com.viehai.identity_service.dto.request.UserUpdateRequest;
import com.viehai.identity_service.dto.response.AddressResponse;
import com.viehai.identity_service.dto.response.JobResponse;
import com.viehai.identity_service.dto.response.UserResponse;
import com.viehai.identity_service.entity.User;
import com.viehai.identity_service.exception.AppException;
import com.viehai.identity_service.exception.ErrorCode;
import com.viehai.identity_service.mapper.UserMapper;
import com.viehai.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile({"mysql","postgres"})
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;

    public User createUser(UserCreateRequest request){

        if(userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTS);
        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);

    }

    @Cacheable(value = "allUsers", key = "'all'")
    public List<UserResponse> getUsers() {
        System.out.println("Querying DB...");

        return userRepository.findAll().stream()
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .firstName(u.getFirstName())
                        .lastName(u.getLastName())
                        .dob(u.getDob())
                        .jobs(u.getJobs().stream()
                                .map(j -> new JobResponse(j.getId(), j.getCode(), j.getName()))
                                .toList())
                        .address(u.getAddress() == null ? null :
                                new AddressResponse(
                                        u.getAddress().getId(),
                                        u.getAddress().getLine1(),
                                        u.getAddress().getCity(),
                                        u.getAddress().getCountry()
                                ))
                        .build()
                )
                .toList();
    }


    public UserResponse getUser(String id){
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }
}
