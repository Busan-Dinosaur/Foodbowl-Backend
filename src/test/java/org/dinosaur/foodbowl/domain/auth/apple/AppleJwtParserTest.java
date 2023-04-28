package org.dinosaur.foodbowl.domain.auth.apple;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class AppleJwtParserTest {

    private final AppleJwtParser appleJwtParser = new AppleJwtParser();

    @Test
    @DisplayName("Apple Jwt 토큰에서 헤더 정보를 추출한다.")
    void extractHeaders() throws NoSuchAlgorithmException {
        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        String appleToken = Jwts.builder()
                .setHeaderParam("kid", "W2R4HXF3K")
                .claim("id", "1234")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setExpiration(new Date(now.getTime() + 1000 * 60 * 60 * 24))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

        Map<String, String> headers = appleJwtParser.extractHeaders(appleToken);

        assertThat(headers).containsKeys("alg", "kid");
    }

    @Test
    @DisplayName("올바르지 않은 Apple Jwt 토큰에서 헤더 정보를 추출하면 예외를 던진다.")
    void extractHeadersWithInvalidToken() {
        assertThatThrownBy(() -> appleJwtParser.extractHeaders("invalidToken"))
                .isInstanceOf(FoodbowlException.class)
                .hasMessage("올바르지 않은 애플 OAuth 토큰입니다.");
    }

    @Test
    @DisplayName("Apple Jwt 토큰, Public Key를 받아 Claims를 추출한다.")
    void extractClaims() throws NoSuchAlgorithmException {
        String subject = "9876";
        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        String appleToken = Jwts.builder()
                .setHeaderParam("kid", "W2R4HXF3K")
                .claim("id", "1234")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setSubject(subject)
                .setExpiration(new Date(now.getTime() + 1000 * 60 * 60 * 24))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

        Claims claims = appleJwtParser.extractClaims(appleToken, publicKey);

        assertAll(
                () -> assertThat(claims).isNotEmpty(),
                () -> assertThat(claims.getSubject()).isEqualTo(subject)
        );
    }

    @Test
    @DisplayName("만료 기간이 지난 토큰에서 Claims를 추출하면 예외를 던진다.")
    void extractClaimsWithExpiredToken() throws NoSuchAlgorithmException {
        String subject = "9876";
        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        String appleToken = Jwts.builder()
                .setHeaderParam("kid", "W2R4HXF3K")
                .claim("id", "1234")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setSubject(subject)
                .setExpiration(new Date(now.getTime() - 1000 * 60 * 60 * 24))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

        assertThatThrownBy(() -> appleJwtParser.extractClaims(appleToken, publicKey))
                .isInstanceOf(FoodbowlException.class)
                .hasMessage("만료된 토큰입니다.");
    }

    @Test
    @DisplayName("유효하지 않은 Apple Jwt 토큰에서 Claims를 추출하면 예외를 던진다.")
    void extractClaimsWithInvalidToken() throws NoSuchAlgorithmException {
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();

        assertThatThrownBy(() -> appleJwtParser.extractClaims("invalidToken", publicKey))
                .isInstanceOf(FoodbowlException.class)
                .hasMessage("손상된 토큰입니다.");
    }

    @Test
    @DisplayName("암호화 방식이 다른 토큰에서 Claims를 추출하면 예외를 던진다.")
    void extractClaimsWithNotSecuredToken() throws NoSuchAlgorithmException {
        String subject = "9876";
        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        KeyPair invalidKeyPair = KeyPairGenerator.getInstance("DSA")
                .generateKeyPair();
        PublicKey publicKey = invalidKeyPair.getPublic();
        String appleToken = Jwts.builder()
                .setHeaderParam("kid", "W2R4HXF3K")
                .claim("id", "1234")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setSubject(subject)
                .setExpiration(new Date(now.getTime() + 1000 * 60 * 60 * 24))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

        assertThatThrownBy(() -> appleJwtParser.extractClaims(appleToken, publicKey))
                .isInstanceOf(FoodbowlException.class)
                .hasMessage("지원하지 않는 토큰입니다.");
    }
}
