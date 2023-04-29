package org.dinosaur.foodbowl.domain.auth.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.auth.application.AuthService;
import org.dinosaur.foodbowl.domain.auth.dto.request.AppleLoginRequestDto;
import org.dinosaur.foodbowl.domain.auth.dto.response.AppleTokenResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/apple/login")
    public ResponseEntity<AppleTokenResponseDto> appleLogin(@Valid @RequestBody AppleLoginRequestDto request) {
        AppleTokenResponseDto response = authService.appleLogin(request);
        return ResponseEntity.ok(response);
    }
}
