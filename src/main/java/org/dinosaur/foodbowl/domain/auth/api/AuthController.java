package org.dinosaur.foodbowl.domain.auth.api;

import static org.dinosaur.foodbowl.global.config.security.jwt.JwtConstant.REFRESH_TOKEN;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.auth.application.AuthService;
import org.dinosaur.foodbowl.domain.auth.dto.FoodbowlTokenDto;
import org.dinosaur.foodbowl.domain.auth.dto.request.AppleLoginRequest;
import org.dinosaur.foodbowl.domain.auth.dto.response.AppleTokenResponse;
import org.dinosaur.foodbowl.domain.auth.dto.response.NicknameDuplicateCheckResponse;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.global.resolver.MemberId;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Validated
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
        Cookie cookie = new Cookie(REFRESH_TOKEN.getName(), refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) jwtTokenProvider.getValidRefreshMilliSecond() / 1000);
        response.addCookie(cookie);
    }

    @PostMapping("/apple/logout")
    public ResponseEntity<Void> appleLogout(@MemberId Long memberId, HttpServletResponse response) {
        authService.appleLogout(memberId);
        deleteRefreshCookie(response);
        return ResponseEntity.noContent().build();
    }

    private void deleteRefreshCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN.getName(), null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<NicknameDuplicateCheckResponse> checkDuplicate(
            @Pattern(regexp = "^[a-zA-Z가-힣0-9]{1,16}$", message = "닉네임은 1자 이상 16자 이하 한글, 영문, 숫자만 가능합니다")
            @RequestParam String nickname
    ) {
        return ResponseEntity.ok(authService.checkDuplicate(nickname));
    }
}
