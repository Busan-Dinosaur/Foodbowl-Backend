package org.dinosaur.foodbowl.domain.auth.application.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.jsonwebtoken.Claims;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.member.domain.vo.RoleType;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JwtTokenProviderTest {

    private static final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
            "5kA0iR3h8K5Z0cS9F6V6t2mE8J4mI1oR7iA9cH2vG5pE8H3lR7yU1tE7C8lO1aV2",
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
    class 클레임_추출 {

        @Test
        void 유효하지_않은_토큰이라면_빈값을_반환한다() {
            String token = "invalid token";

            Optional<Claims> result = jwtTokenProvider.extractClaims(token);

            assertThat(result).isEmpty();
        }

        @Test
        void 유효한_토큰이라면_클레임을_추출해서_반환한다() {
            String token = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

            Optional<Claims> result = jwtTokenProvider.extractClaims(token);

            assertThat(result).isNotEmpty();
        }
    }
}
