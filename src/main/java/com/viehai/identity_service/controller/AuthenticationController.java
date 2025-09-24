package com.viehai.identity_service.controller;

import com.viehai.identity_service.dto.response.ApiResponse;
import com.viehai.identity_service.dto.request.AuthenticationRequest;
import com.viehai.identity_service.dto.response.AuthenticationResponse;
import com.viehai.identity_service.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/log-in")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
       boolean result = authenticationService.authenticate(request);
       return ApiResponse.<AuthenticationResponse>builder()
               .result(AuthenticationResponse.builder().authenticated(result).build())
               .build();
    }

}
