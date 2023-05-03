package org.dinosaur.foodbowl.domain.auth.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.auth.application.AuthService;
import org.dinosaur.foodbowl.domain.auth.dto.FoodbowlTokenDto;
import org.dinosaur.foodbowl.domain.auth.dto.request.AppleLoginRequest;
import org.dinosaur.foodbowl.domain.auth.dto.response.AppleTokenResponse;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/apple/login")
    public ResponseEntity<AppleTokenResponse> appleLogin(
            @Valid @RequestBody AppleLoginRequest request,
            HttpServletResponse response
    ) {
        FoodbowlTokenDto foodbowlTokenDto = authService.appleLogin(request);
        registerRefreshCookie(response, foodbowlTokenDto.getRefreshToken());
        return ResponseEntity.ok(new AppleTokenResponse(foodbowlTokenDto.getAccessToken()));
    }

    private void registerRefreshCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) jwtTokenProvider.getValidRefreshMilliSecond() / 1000);
        response.addCookie(cookie);
    }
}
