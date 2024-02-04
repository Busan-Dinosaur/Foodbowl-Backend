package org.dinosaur.foodbowl.domain.auth.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.auth.application.AuthService;
import org.dinosaur.foodbowl.domain.auth.dto.reqeust.AppleLoginRequest;
import org.dinosaur.foodbowl.domain.auth.dto.reqeust.RenewTokenRequest;
import org.dinosaur.foodbowl.domain.auth.dto.response.TokenResponse;
import org.dinosaur.foodbowl.global.presentation.Auth;
import org.dinosaur.foodbowl.global.presentation.LoginMember;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@RestController
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    @PostMapping("/login/oauth/apple")
    public ResponseEntity<TokenResponse> appleLogin(@RequestBody @Valid AppleLoginRequest appleLoginRequest) {
        TokenResponse response = authService.appleLogin(appleLoginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Auth LoginMember loginMember) {
        authService.logout(loginMember);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/token/renew")
    public ResponseEntity<TokenResponse> renewToken(@RequestBody @Valid RenewTokenRequest renewTokenRequest) {
        TokenResponse response = authService.renewToken(renewTokenRequest);
        return ResponseEntity.ok(response);
    }
}
