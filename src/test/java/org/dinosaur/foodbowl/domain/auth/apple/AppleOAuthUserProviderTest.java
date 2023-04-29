package org.dinosaur.foodbowl.domain.auth.apple;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.auth.dto.response.ApplePlatformUserResponseDto;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.security.PublicKey;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AppleOAuthUserProviderTest extends IntegrationTest {

    @Autowired
    private AppleOAuthUserProvider appleOAuthUserProvider;

    @MockBean
    private AppleJwtParser appleJwtParser;
    @MockBean
    private AppleClient appleClient;
    @MockBean
    private PublicKeyGenerator publicKeyGenerator;
    @MockBean
    private AppleClaimsValidator appleClaimsValidator;

    @Test
    @DisplayName("유효한 Claims를 가진 토큰이면 애플 유저 정보를 추출한다.")
    void extractApplePlatformUser() {
        Claims claims = Jwts.claims()
                .setSubject("subject");
        claims.put("email", "email");

        given(appleJwtParser.extractHeaders(anyString())).willReturn(mock(Map.class));
        given(appleClient.getApplePublicKeys()).willReturn(mock(ApplePublicKeys.class));
        given(publicKeyGenerator.generatePublicKey(any(), any())).willReturn(mock(PublicKey.class));
        given(appleJwtParser.extractClaims(anyString(), any())).willReturn(claims);
        given(appleClaimsValidator.isValid(any())).willReturn(true);

        ApplePlatformUserResponseDto result = appleOAuthUserProvider.extractApplePlatformUser("validToken");

        assertAll(
                () -> assertThat(result.getSocialId()).isEqualTo("subject"),
                () -> assertThat(result.getEmail()).isEqualTo("email")
        );
    }

    @Test
    @DisplayName("유효하지 않은 Claims를 가진 토큰이면 예외를 던진다.")
    void extractApplePlatformUserWithInvalidClaims() {
        Claims claims = Jwts.claims()
                .setSubject("InvalidSubject");
        claims.put("email", "InvalidEmail");

        given(appleJwtParser.extractHeaders(anyString())).willReturn(mock(Map.class));
        given(appleClient.getApplePublicKeys()).willReturn(mock(ApplePublicKeys.class));
        given(publicKeyGenerator.generatePublicKey(any(), any())).willReturn(mock(PublicKey.class));
        given(appleJwtParser.extractClaims(anyString(), any())).willReturn(claims);
        given(appleClaimsValidator.isValid(any())).willReturn(false);

        assertThatThrownBy(() -> appleOAuthUserProvider.extractApplePlatformUser("InvalidToken"))
                .isInstanceOf(FoodbowlException.class)
                .hasMessage("올바르지 않은 애플 OAuth 토큰 조각 정보입니다.");

    }
}
