package org.dinosaur.foodbowl.domain.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import io.jsonwebtoken.Claims;
import org.dinosaur.foodbowl.domain.auth.application.apple.AppleOAuthUserProvider;
import org.dinosaur.foodbowl.domain.auth.application.dto.AppleUser;
import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.auth.dto.reqeust.AppleLoginRequest;
import org.dinosaur.foodbowl.domain.auth.dto.response.TokenResponse;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.dinosaur.foodbowl.test.IntegrationTest;
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
}
