package org.dinosaur.foodbowl.domain.auth.application;

import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.auth.apple.AppleOAuthUserProvider;
import org.dinosaur.foodbowl.domain.auth.dto.FoodbowlTokenDto;
import org.dinosaur.foodbowl.domain.auth.dto.request.AppleLoginRequestDto;
import org.dinosaur.foodbowl.domain.auth.dto.response.ApplePlatformUserResponseDto;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.member.repository.MemberRepository;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class AuthServiceTest extends IntegrationTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private AppleOAuthUserProvider appleOAuthUserProvider;

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
            AppleLoginRequestDto appleLoginRequestDto = new AppleLoginRequestDto("AppleToken");
            ApplePlatformUserResponseDto applePlatformUserResponseDto = new ApplePlatformUserResponseDto(
                    "1234",
                    "member@foodbowl.com"
            );

            given(appleOAuthUserProvider.extractApplePlatformUser(anyString())).willReturn(applePlatformUserResponseDto);

            FoodbowlTokenDto result = authService.appleLogin(appleLoginRequestDto);

            assertAll(
                    () -> assertThat(result.getAccessToken()).isNotNull(),
                    () -> assertThat(result.getRefreshToken()).isNotNull()
            );

            redisTemplate.delete(String.valueOf(member.getId()));
        }

        @Test
        @DisplayName("이전에 회원가입을 하지 않은 회원이라면 예외를 던진다.")
        void appleLoginWithNotRegistered() {
            AppleLoginRequestDto appleLoginRequestDto = new AppleLoginRequestDto("AppleToken");
            ApplePlatformUserResponseDto applePlatformUserResponseDto = new ApplePlatformUserResponseDto(
                    "1234",
                    "member@foodbowl.com"
            );

            given(appleOAuthUserProvider.extractApplePlatformUser(anyString())).willReturn(applePlatformUserResponseDto);

            assertThatThrownBy(() -> authService.appleLogin(appleLoginRequestDto))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage("애플 회원가입이 되지 않은 회원입니다.");
        }
    }
}
