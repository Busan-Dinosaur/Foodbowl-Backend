package org.dinosaur.foodbowl.domain.auth.application.apple;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AppleTokenParserTest {

    private static final AppleTokenParser appleTokenParser = new AppleTokenParser(new ObjectMapper());

    @Nested
    class 애플_토큰_헤더_추출_시 {

        @Test
        void 정상적인_애플_토큰을_파싱하여_헤더_정보를_추출한다() throws NoSuchAlgorithmException {
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

            Map<String, String> headers = appleTokenParser.extractHeaders(appleToken);

            assertThat(headers).containsKeys("alg", "kid");
        }

        @Test
        void 정상적이지_않은_헤더를_가진_애플_토큰을_파싱하면_예외를_던진다() {
            String invalidHeader = Base64.getUrlEncoder().encodeToString("invalid json".getBytes());
            String appleToken = invalidHeader + ".payload.signature";

            assertThatThrownBy(() -> appleTokenParser.extractHeaders(appleToken))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("헤더가 유효하지 않은 토큰입니다.");
        }

        @Test
        void 정상적이지_않은_애플_토큰을_파싱하면_예외를_던진다() {
            assertThatThrownBy(() -> appleTokenParser.extractHeaders("Invalid Token"))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("Base64로 디코딩할 수 없는 값입니다.");
        }
    }

    @Nested
    class 애플_클레임_추출_시 {

        @Test
        void 정상적인_애플_토큰이라면_클레임을_추출한다() throws NoSuchAlgorithmException {
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

            Claims claims = appleTokenParser.extractClaims(appleToken, publicKey);

            assertSoftly(softly -> {
                softly.assertThat(claims).isNotEmpty();
                softly.assertThat(claims.getSubject()).isEqualTo(subject);
                softly.assertThat(claims.getAudience()).isEqualTo("aud");
            });
        }

        @Test
        void 만료기간이_지난_애플_토큰이라면_예외를_던진다() throws NoSuchAlgorithmException {
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

            assertThatThrownBy(() -> appleTokenParser.extractClaims(appleToken, publicKey))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("만료된 토큰입니다.");
        }

        @Test
        void 지원하지_않는_애플_토큰이라면_예외를_던진다() throws NoSuchAlgorithmException {
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

            assertThatThrownBy(() -> appleTokenParser.extractClaims(appleToken, publicKey))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("지원하지 않는 토큰입니다.");
        }

        @Test
        void 손상된_애플_토큰이라면_예외를_던진다() throws NoSuchAlgorithmException {
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                    .generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();

            assertThatThrownBy(() -> appleTokenParser.extractClaims("invalidToken", publicKey))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("손상된 토큰입니다.");
        }
    }
}
