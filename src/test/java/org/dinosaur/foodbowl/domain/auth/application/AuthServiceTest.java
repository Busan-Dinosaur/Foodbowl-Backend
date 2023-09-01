package org.dinosaur.foodbowl.domain.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import io.jsonwebtoken.Claims;
import java.util.concurrent.TimeUnit;
import org.dinosaur.foodbowl.domain.auth.application.apple.AppleOAuthUserProvider;
import org.dinosaur.foodbowl.domain.auth.application.dto.AppleUser;
import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.auth.dto.reqeust.AppleLoginRequest;
import org.dinosaur.foodbowl.domain.auth.dto.reqeust.RenewTokenRequest;
import org.dinosaur.foodbowl.domain.auth.dto.response.RenewTokenResponse;
import org.dinosaur.foodbowl.domain.auth.dto.response.TokenResponse;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.RoleType;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.dinosaur.foodbowl.global.exception.AuthenticationException;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;

@SuppressWarnings("NonAsciiCharacters")
class AuthServiceTest extends IntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private AppleOAuthUserProvider appleOAuthUserProvider;

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Nested
    class 애플_로그인 {

        @Test
        void 등록된_회원이라면_토큰_정보를_반환한다() {
            Member member = memberTestPersister.memberBuilder()
                    .socialType(SocialType.APPLE)
                    .socialId("1234")
                    .email("email@email.com")
                    .save();
            AppleUser platformUser = new AppleUser(SocialType.APPLE, "1234", "email@email.com");
            given(appleOAuthUserProvider.extractPlatformUser(anyString())).willReturn(platformUser);

            AppleLoginRequest appleLoginRequest = new AppleLoginRequest("token");
            TokenResponse tokenResponse = authService.appleLogin(appleLoginRequest);

            String saveRefreshToken = (String) redisTemplate.opsForValue().get(String.valueOf(member.getId()));
            assertThat(saveRefreshToken).isEqualTo(tokenResponse.refreshToken());
        }

        @Test
        void 등록_되어있지_않은_회원이라면_등록_후_토큰_정보를_반환한다() {
            AppleUser platformUser = new AppleUser(SocialType.APPLE, "1234", "email@email.com");
            given(appleOAuthUserProvider.extractPlatformUser(anyString())).willReturn(platformUser);

            AppleLoginRequest appleLoginRequest = new AppleLoginRequest("token");
            TokenResponse tokenResponse = authService.appleLogin(appleLoginRequest);

            Claims claims = jwtTokenProvider.extractClaims(tokenResponse.accessToken()).get();
            String memberId = claims.getSubject();
            String saveRefreshToken = (String) redisTemplate.opsForValue().get(memberId);
            assertSoftly(softly -> {
                softly.assertThat(saveRefreshToken).isEqualTo(tokenResponse.refreshToken());
                softly.assertThat(memberRepository.findById(Long.valueOf(memberId))).isPresent();
            });
        }
    }

    @Nested
    class 토큰_갱신 {

        @Test
        void 유효한_인증_리프레쉬_토큰이라면_새로운_인증_토큰을_반환한다() {
            String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);
            String refreshToken = jwtTokenProvider.createRefreshToken(1L);
            redisTemplate.opsForValue().set("1", refreshToken, 10000, TimeUnit.MILLISECONDS);
            RenewTokenRequest renewTokenRequest = new RenewTokenRequest(accessToken, refreshToken);

            RenewTokenResponse response = authService.renewToken(renewTokenRequest);

            assertSoftly(softly -> {
                softly.assertThat(response.accessToken()).isNotNull();
                softly.assertThat(jwtTokenProvider.extractSubject(response.accessToken())).isEqualTo("1");
            });
        }

        @Test
        void 인증기간이_지난_인증_토큰이라면_새로운_인증_토큰을_반환한다() {
            //EXPIRED DATE : 2023-08-29 14:08
            String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOiJST0xFX-2ajOybkCIsImlhdCI6MTY5MzI4NTcxMywiZXhwIjoxNjkzMjg1NzEzfQ.R5FQG2BisLtvgHFx19T336l31uKxIO24knafadG1B2I";
            String refreshToken = jwtTokenProvider.createRefreshToken(1L);
            redisTemplate.opsForValue().set("1", refreshToken, 10000, TimeUnit.MILLISECONDS);
            RenewTokenRequest renewTokenRequest = new RenewTokenRequest(accessToken, refreshToken);

            RenewTokenResponse response = authService.renewToken(renewTokenRequest);

            assertSoftly(softly -> {
                softly.assertThat(response.accessToken()).isNotNull();
                softly.assertThat(jwtTokenProvider.extractSubject(response.accessToken())).isEqualTo("1");
            });
        }

        @Test
        void 저장소에_리프레쉬_토큰이_존재하지_않으면_예외를_던진다() {
            String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);
            String refreshToken = jwtTokenProvider.createRefreshToken(1L);
            RenewTokenRequest renewTokenRequest = new RenewTokenRequest(accessToken, refreshToken);

            assertThatThrownBy(() -> authService.renewToken(renewTokenRequest))
                    .isInstanceOf(AuthenticationException.class)
                    .hasMessage("리프레쉬 토큰 저장이 만료되었습니다.");
        }

        @Test
        void 유효하지_않은_인증_토큰이라면_예외를_던진다() {
            String accessToken = "invalid access token";
            String refreshToken = jwtTokenProvider.createRefreshToken(1L);
            RenewTokenRequest renewTokenRequest = new RenewTokenRequest(accessToken, refreshToken);

            assertThatThrownBy(() -> authService.renewToken(renewTokenRequest))
                    .isInstanceOf(AuthenticationException.class)
                    .hasMessage("손상된 토큰입니다.");
        }

        @Test
        void 유효하지_않은_리프레쉬_토큰이라면_예외를_던진다() {
            String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);
            String refreshToken = "invali token";
            RenewTokenRequest renewTokenRequest = new RenewTokenRequest(accessToken, refreshToken);

            assertThatThrownBy(() -> authService.renewToken(renewTokenRequest))
                    .isInstanceOf(AuthenticationException.class)
                    .hasMessage("손상된 토큰입니다.");
        }

        @Test
        void 인증_토큰에_맞지_않은_리프레쉬_토큰이라면_예외를_던진다() {
            String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);
            String refreshToken = jwtTokenProvider.createRefreshToken(1L);
            String otherRefreshToken = jwtTokenProvider.createRefreshToken(2L);
            redisTemplate.opsForValue().set("1", refreshToken, 10000, TimeUnit.MILLISECONDS);
            RenewTokenRequest renewTokenRequest = new RenewTokenRequest(accessToken, otherRefreshToken);

            assertThatThrownBy(() -> authService.renewToken(renewTokenRequest))
                    .isInstanceOf(AuthenticationException.class)
                    .hasMessage("리프레쉬 토큰이 일치하지 않습니다.");
        }
    }
}
