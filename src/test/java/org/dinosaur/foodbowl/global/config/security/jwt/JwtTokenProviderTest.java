package org.dinosaur.foodbowl.global.config.security.jwt;

import org.dinosaur.foodbowl.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dinosaur.foodbowl.domain.member.entity.Role.RoleType.ROLE_회원;

class JwtTokenProviderTest extends IntegrationTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("엑세스 토큰을 생성한다.")
    void createAccessToken() {
        String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        Authentication authentication = jwtTokenProvider.generateAuth(accessToken).get();
        List<String> roleNames = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertThat(roleNames).containsExactly(ROLE_회원.name());
    }

    @Test
    @DisplayName("리프레쉬 토큰을 생성한다.")
    void createRefreshToken() {
        String refreshToken = jwtTokenProvider.createRefreshToken(1L);

        String id = jwtTokenProvider.extractSubject(refreshToken).get();

        assertThat(id).isEqualTo("1");
    }

    @Test
    @DisplayName("유효하지 않은 토큰에서 인증 정보를 얻을 수 없다.")
    void getAuthenticationByInvalidToken() {
        String accessToken = "invalid-token";

        Optional<Authentication> authentication = jwtTokenProvider.generateAuth(accessToken);

        assertThat(authentication).isEmpty();
    }

    @Test
    @DisplayName("유효하지 않은 토큰에서 제목 정보를 얻을 수 없다.")
    void getSubjectByInvalidToken() {
        String accessToken = "invalid-token";

        Optional<String> subject = jwtTokenProvider.extractSubject(accessToken);

        assertThat(subject).isEmpty();
    }

    @Test
    @DisplayName("만료된 토큰에서 인증 정보를 얻을 수 없다.")
    void getAuthenticationByExpiredToken() {
        JwtTokenProvider expiredJwtTokenProvider = new JwtTokenProvider(
                "4944284173b906fc7409a2de29391af31c45b55abc248bb4807c52334f800e308c5069e3379021fae9c01d78273111721158e99c24c46f217bb50021b3c03953",
                1,
                1
        );
        String accessToken = expiredJwtTokenProvider.createAccessToken(1L, ROLE_회원);

        Optional<Authentication> authentication = jwtTokenProvider.generateAuth(accessToken);

        assertThat(authentication).isEmpty();
    }

    @Test
    @DisplayName("만료된 토큰에서 제목 정보를 얻을 수 없다.")
    void getSubjectByExpiredToken() {
        JwtTokenProvider expiredJwtTokenProvider = new JwtTokenProvider(
                "4944284173b906fc7409a2de29391af31c45b55abc248bb4807c52334f800e308c5069e3379021fae9c01d78273111721158e99c24c46f217bb50021b3c03953",
                1,
                1
        );
        String accessToken = expiredJwtTokenProvider.createAccessToken(1L, ROLE_회원);

        Optional<String> subject = jwtTokenProvider.extractSubject(accessToken);

        assertThat(subject).isEmpty();
    }

    @Test
    @DisplayName("올바르지 않은 키로 생성된 토큰에서 인증 정보를 얻을 수 없다.")
    void getAuthenticationByInvalidKeyToken() {
        JwtTokenProvider invalidJwtTokenProvider = new JwtTokenProvider(
                "4944284173b906fc7409a2de29391af31c45b55abc248bb4807c52334f800e308c5069e3379021fae9c01d78273111721158e99c24c46f217bb50021b3c03953",
                100000,
                100000
        );
        String accessToken = invalidJwtTokenProvider.createAccessToken(1L, ROLE_회원);

        Optional<Authentication> authentication = jwtTokenProvider.generateAuth(accessToken);

        assertThat(authentication).isEmpty();
    }

    @Test
    @DisplayName("올바르지 않은 키로 생성된 토큰에서 제목 정보를 얻을 수 없다.")
    void getSubjectByInvalidKeyToken() {
        JwtTokenProvider invalidJwtTokenProvider = new JwtTokenProvider(
                "4944284173b906fc7409a2de29391af31c45b55abc248bb4807c52334f800e308c5069e3379021fae9c01d78273111721158e99c24c46f217bb50021b3c03953",
                100000,
                100000
        );
        String accessToken = invalidJwtTokenProvider.createAccessToken(1L, ROLE_회원);

        Optional<String> subject = jwtTokenProvider.extractSubject(accessToken);

        assertThat(subject).isEmpty();
    }
}
