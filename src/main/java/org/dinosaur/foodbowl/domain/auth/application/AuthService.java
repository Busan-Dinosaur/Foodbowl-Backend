package org.dinosaur.foodbowl.domain.auth.application;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.auth.apple.AppleOAuthUserProvider;
import org.dinosaur.foodbowl.domain.auth.dto.request.AppleLoginRequestDto;
import org.dinosaur.foodbowl.domain.auth.dto.response.ApplePlatformUserResponseDto;
import org.dinosaur.foodbowl.domain.auth.dto.response.AppleTokenResponseDto;
import org.dinosaur.foodbowl.domain.member.repository.MemberRepository;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.stereotype.Service;

import static org.dinosaur.foodbowl.domain.member.entity.Member.SocialType;
import static org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import static org.dinosaur.foodbowl.global.exception.ErrorStatus.APPLE_NOT_REGISTER;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AppleOAuthUserProvider appleOAuthUserProvider;

    public AppleTokenResponseDto appleLogin(AppleLoginRequestDto appleLoginRequestDto) {
        ApplePlatformUserResponseDto applePlatformUserResponseDto =
                appleOAuthUserProvider.extractApplePlatformUser(appleLoginRequestDto.getAppleToken());
        String socialId = applePlatformUserResponseDto.getSocialId();

        return memberRepository.findBySocialTypeAndSocialId(SocialType.APPLE, socialId)
                .map(member -> {
                    String accessToken = jwtTokenProvider.createAccessToken(member.getId(), RoleType.ROLE_회원);
                    return new AppleTokenResponseDto(accessToken);
                })
                .orElseThrow(() -> new FoodbowlException(APPLE_NOT_REGISTER));
    }
}
