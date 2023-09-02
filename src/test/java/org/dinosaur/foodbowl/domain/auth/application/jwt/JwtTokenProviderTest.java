package org.dinosaur.foodbowl.domain.auth.application.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.dinosaur.foodbowl.domain.member.domain.vo.RoleType;
import org.dinosaur.foodbowl.global.exception.AuthenticationException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JwtTokenProviderTest {

    private static final String secretKey = "5kA0iR3h8K5Z0cS9F6V6t2mE8J4mI1oR7iA9cH2vG5pE8H3lR7yU1tE7C8lO1aV2";
    private static final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
            secretKey,
            60000,
            300000
    );

    @Test
    void 인증_토큰을_생성한다() {
        String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        Claims claims = jwtTokenProvider.extractClaims(accessToken).get();
        assertSoftly(softly -> {
            softly.assertThat(claims.getSubject()).isEqualTo("1");
            softly.assertThat((String) claims.get(JwtConstant.CLAMS_ROLES.getName()))
                    .contains(RoleType.ROLE_회원.toString());
        });
    }

    @Test
    void 갱신_토큰을_생성한다() {
        String refreshToken = jwtTokenProvider.createRefreshToken(1L);

        Claims claims = jwtTokenProvider.extractClaims(refreshToken).get();
        assertThat(claims.getSubject()).isEqualTo("1");
    }

    @Nested
    class 클레임_주제_추출 {

        @Test
        void 유효한_토큰이라면_클레임_주제를_추출해서_반환한다() {
            String token = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

            String result = jwtTokenProvider.extractSubject(token);

            assertThat(result).isEqualTo("1");
        }

        @Test
        void 유효시간이_지난_토큰이라면_클레임_주제를_추출해서_반환한다() {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.claims().setSubject(String.valueOf(1L));
            Date now = new Date();
            Date expiredDate = new Date(now.getTime() - 10000);
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(expiredDate)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            String result = jwtTokenProvider.extractSubject(token);

            assertThat(result).isEqualTo("1");
        }

        @Test
        void 손상된_토큰이라면_예외를_던진다() {
            String token = "invalid token";

            assertThatThrownBy(() -> jwtTokenProvider.extractSubject(token))
                    .isInstanceOf(AuthenticationException.class)
                    .hasMessage("손상된 토큰입니다.");
        }

        @Test
        void 지원하지_않는_토큰이라면_예외를_던진다() {
            String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);
            String[] splitToken = accessToken.split("\\.");
            String token = splitToken[0] + "." + splitToken[1] + ".";

            assertThatThrownBy(() -> jwtTokenProvider.extractSubject(token))
                    .isInstanceOf(AuthenticationException.class)
                    .hasMessage("지원하지 않는 토큰입니다.");
        }

        @Test
        void 시그니처_검증에_실패한_토큰이라면_예외를_던진다() {
            String otherSecretKey = "5kA0iR3h8K5Z0cS9F6V6t2mE8J4mI1oR7iA9cH2vG5pE8H3lR7yU1tE7C8lO1aV3";
            SecretKey key = Keys.hmacShaKeyFor(otherSecretKey.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.claims().setSubject(String.valueOf(1L));
            Date now = new Date();
            Date expiredDate = new Date(now.getTime() + 10000);
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(expiredDate)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            assertThatThrownBy(() -> jwtTokenProvider.extractSubject(token))
                    .isInstanceOf(AuthenticationException.class)
                    .hasMessage("시그니처 검증에 실패한 토큰입니다.");
        }
    }

    @Nested
    class 클레임_추출 {

        @Test
        void 유효한_토큰이라면_클레임을_추출해서_반환한다() {
            String token = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

            Optional<Claims> result = jwtTokenProvider.extractClaims(token);

            assertThat(result).isNotEmpty();
        }

        @Test
        void 유효하지_않은_토큰이라면_빈값을_반환한다() {
            String token = "invalid token";

            Optional<Claims> result = jwtTokenProvider.extractClaims(token);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class 유효한_클레임_추출 {

        @Test
        void 유효한_토큰이라면_클레임_주제는_회원ID이다() {
            String token = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

            Claims claims = jwtTokenProvider.extractValidClaims(token);

            assertThat(claims.getSubject()).isEqualTo("1");
        }

        @Test
        void 유효시간이_지난_토큰이라면_예외를_던진다() {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.claims().setSubject(String.valueOf(1L));
            Date now = new Date();
            Date expiredDate = new Date(now.getTime() - 10000);
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(expiredDate)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            assertThatThrownBy(() -> jwtTokenProvider.extractValidClaims(token))
                    .isInstanceOf(AuthenticationException.class)
                    .hasMessage("만료된 토큰입니다.");
        }

        @Test
        void 손상된_토큰이라면_예외를_던진다() {
            String token = "invalid token";

            assertThatThrownBy(() -> jwtTokenProvider.extractValidClaims(token))
                    .isInstanceOf(AuthenticationException.class)
                    .hasMessage("손상된 토큰입니다.");
        }

        @Test
        void 지원하지_않는_토큰이라면_예외를_던진다() {
            String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);
            String[] splitToken = accessToken.split("\\.");
            String token = splitToken[0] + "." + splitToken[1] + ".";

            assertThatThrownBy(() -> jwtTokenProvider.extractValidClaims(token))
                    .isInstanceOf(AuthenticationException.class)
                    .hasMessage("지원하지 않는 토큰입니다.");
        }

        @Test
        void 시그니처_검증에_실패한_토큰이라면_예외를_던진다() {
            String otherSecretKey = "5kA0iR3h8K5Z0cS9F6V6t2mE8J4mI1oR7iA9cH2vG5pE8H3lR7yU1tE7C8lO1aV3";
            SecretKey key = Keys.hmacShaKeyFor(otherSecretKey.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.claims().setSubject(String.valueOf(1L));
            Date now = new Date();
            Date expiredDate = new Date(now.getTime() + 10000);
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(expiredDate)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            assertThatThrownBy(() -> jwtTokenProvider.extractValidClaims(token))
                    .isInstanceOf(AuthenticationException.class)
                    .hasMessage("시그니처 검증에 실패한 토큰입니다.");
        }
    }
}
