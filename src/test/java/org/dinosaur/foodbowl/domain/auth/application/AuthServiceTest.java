package org.dinosaur.foodbowl.domain.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.util.concurrent.TimeUnit;
import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.auth.apple.AppleOAuthUserProvider;
import org.dinosaur.foodbowl.domain.auth.dto.FoodbowlTokenDto;
import org.dinosaur.foodbowl.domain.auth.dto.request.AppleLoginRequest;
import org.dinosaur.foodbowl.domain.auth.dto.response.ApplePlatformUserResponse;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.member.repository.MemberRepository;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;

class AuthServiceTest extends IntegrationTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private AppleOAuthUserProvider appleOAuthUserProvider;

    @Test
    @DisplayName("애플 로그아웃은 리프레쉬 토큰을 삭제한다.")
    void appleLogout() {
        Long memberId = 1L;
        redisTemplate.opsForValue().set(
                String.valueOf(memberId),
                "refreshToken",
                5,
                TimeUnit.MINUTES
        );

        authService.appleLogout(memberId);

        assertThat(redisTemplate.opsForValue().get(String.valueOf(memberId))).isNull();
    }

    @Nested
    @DisplayName("appleLogin 메서드는 ")
    class AppleLogin {

        @Test
        @DisplayName("이전에 회원가입을 한 회원이라면 서버에서 발급한 Jwt 토큰을 반환한다.")
        void appleLogin() {
            Member member = Member.builder()
                    .socialType(Member.SocialType.APPLE)
                    .socialId("1234")
                    .nickname("member1234")
                    .build();
            memberRepository.save(member);
            AppleLoginRequest appleLoginRequest = new AppleLoginRequest("AppleToken");
            ApplePlatformUserResponse applePlatformUserResponse = new ApplePlatformUserResponse(
                    "1234",
                    "member@foodbowl.com"
            );

            given(appleOAuthUserProvider.extractApplePlatformUser(anyString())).willReturn(applePlatformUserResponse);

            FoodbowlTokenDto result = authService.appleLogin(appleLoginRequest);

            assertAll(
                    () -> assertThat(result.getAccessToken()).isNotNull(),
                    () -> assertThat(result.getRefreshToken()).isNotNull()
            );

            redisTemplate.delete(String.valueOf(member.getId()));
        }

        @Test
        @DisplayName("이전에 회원가입을 하지 않은 회원이라면 예외를 던진다.")
        void appleLoginWithNotRegistered() {
            AppleLoginRequest appleLoginRequest = new AppleLoginRequest("AppleToken");
            ApplePlatformUserResponse applePlatformUserResponse = new ApplePlatformUserResponse(
                    "1234",
                    "member@foodbowl.com"
            );

            given(appleOAuthUserProvider.extractApplePlatformUser(anyString())).willReturn(applePlatformUserResponse);

            assertThatThrownBy(() -> authService.appleLogin(appleLoginRequest))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage("애플 회원가입이 되지 않은 회원입니다.");
        }
    }
}
