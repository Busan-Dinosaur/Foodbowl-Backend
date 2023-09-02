package org.dinosaur.foodbowl.domain.auth.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.auth.application.AuthService;
import org.dinosaur.foodbowl.domain.auth.dto.reqeust.AppleLoginRequest;
import org.dinosaur.foodbowl.domain.auth.dto.reqeust.RenewTokenRequest;
import org.dinosaur.foodbowl.domain.auth.dto.response.RenewTokenResponse;
import org.dinosaur.foodbowl.domain.auth.dto.response.TokenResponse;
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

    @PostMapping("/token/renew")
    public ResponseEntity<RenewTokenResponse> renewToken(@RequestBody @Valid RenewTokenRequest renewTokenRequest) {
        RenewTokenResponse response = authService.renewToken(renewTokenRequest);
        return ResponseEntity.ok(response);
    }
}
