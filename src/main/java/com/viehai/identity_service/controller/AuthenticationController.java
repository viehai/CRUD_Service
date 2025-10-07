package com.viehai.identity_service.controller;

import com.viehai.identity_service.dto.response.ApiResponse;
import com.viehai.identity_service.dto.request.AuthenticationRequest;
import com.viehai.identity_service.dto.response.AuthenticationResponse;
import com.viehai.identity_service.entity.User;
import com.viehai.identity_service.service.AuthenticationService;
import com.viehai.identity_service.service.OAuth2Service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    OAuth2Service oAuth2Service;

    @PostMapping("/log-in")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
       boolean result = authenticationService.authenticate(request);
       return ApiResponse.<AuthenticationResponse>builder()
               .result(AuthenticationResponse.builder().authenticated(result).build())
               .build();
    }

    @GetMapping("/login/google")
    public RedirectView googleLogin(jakarta.servlet.http.HttpServletRequest request) {
        String target = request.getContextPath() + "/oauth2/authorization/google";
        return new RedirectView(target);
    }

    @GetMapping("/oauth2/success")
    ApiResponse<Map<String, Object>> oauth2Success(@AuthenticationPrincipal OAuth2User oauth2User) {
        User user = oAuth2Service.processOAuth2User(oauth2User);
        
        return ApiResponse.<Map<String, Object>>builder()
                .result(Map.of(
                    "authenticated", true,
                    "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName()
                    )
                ))
                .build();
    }

    @GetMapping("/oauth2/failure")
    ApiResponse<Map<String, Object>> oauth2Failure(@RequestParam(value = "error", required = false) String error) {
        return ApiResponse.<Map<String, Object>>builder()
                .result(Map.of(
                        "authenticated", false,
                        "error", error != null ? error : "OAuth2 authentication failed"
                ))
                .build();
    }
}
