package org.dinosaur.foodbowl.domain.auth.application;

import static org.dinosaur.foodbowl.domain.member.entity.Member.SocialType;
import static org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import static org.dinosaur.foodbowl.global.exception.ErrorStatus.APPLE_NOT_REGISTER;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.auth.apple.AppleOAuthUserProvider;
import org.dinosaur.foodbowl.domain.auth.dto.FoodbowlTokenDto;
import org.dinosaur.foodbowl.domain.auth.dto.request.AppleLoginRequest;
import org.dinosaur.foodbowl.domain.auth.dto.response.ApplePlatformUserResponse;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.member.repository.MemberRepository;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AppleOAuthUserProvider appleOAuthUserProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public FoodbowlTokenDto appleLogin(AppleLoginRequest appleLoginRequest) {
        ApplePlatformUserResponse applePlatformUserResponse =
                appleOAuthUserProvider.extractApplePlatformUser(appleLoginRequest.getAppleToken());
        String socialId = applePlatformUserResponse.getSocialId();

        final Member member = memberRepository.findBySocialTypeAndSocialId(SocialType.APPLE, socialId)
                .orElseThrow(() -> new FoodbowlException(APPLE_NOT_REGISTER));

        return generateFoodbowlToken(member);
    }

    private FoodbowlTokenDto generateFoodbowlToken(Member member) {
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), RoleType.ROLE_회원);
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        registerRefreshToken(member.getId(), refreshToken);
        return new FoodbowlTokenDto(accessToken, refreshToken);
    }

    private void registerRefreshToken(Long memberId, String refreshToken) {
        redisTemplate.opsForValue()
                .set(
                        String.valueOf(memberId),
                        refreshToken,
                        jwtTokenProvider.getValidRefreshMilliSecond(),
                        TimeUnit.MILLISECONDS
                );
    }

    @Transactional
    public void appleLogout(Long memberId) {
        redisTemplate.delete(String.valueOf(memberId));
    }
}
