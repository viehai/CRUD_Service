package com.viehai.identity_service.service;

import com.viehai.identity_service.dto.request.AuthenticationRequest;
import com.viehai.identity_service.dto.response.AuthenticationResponse;
import com.viehai.identity_service.exception.AppException;
import com.viehai.identity_service.exception.ErrorCode;
import com.viehai.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    JwtService jwtService;

     public AuthenticationResponse authenticate(AuthenticationRequest request){
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        
        if (!isAuthenticated) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        String token = jwtService.generateToken(user);
        
        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }
}
