package org.dinosaur.foodbowl.domain.auth.application;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.auth.application.apple.AppleOAuthUserProvider;
import org.dinosaur.foodbowl.domain.auth.application.dto.AppleUser;
import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenValid;
import org.dinosaur.foodbowl.domain.auth.dto.reqeust.AppleLoginRequest;
import org.dinosaur.foodbowl.domain.auth.dto.reqeust.RenewTokenRequest;
import org.dinosaur.foodbowl.domain.auth.dto.response.RenewTokenResponse;
import org.dinosaur.foodbowl.domain.auth.dto.response.TokenResponse;
import org.dinosaur.foodbowl.domain.auth.exception.AuthExceptionType;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.Nickname;
import org.dinosaur.foodbowl.domain.member.domain.vo.RoleType;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.dinosaur.foodbowl.global.exception.AuthenticationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final AppleOAuthUserProvider appleOAuthUserProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public TokenResponse appleLogin(AppleLoginRequest appleLoginRequest) {
        AppleUser platformUser = appleOAuthUserProvider.extractPlatformUser(appleLoginRequest.appleToken());
        return memberRepository.findBySocialTypeAndSocialId(platformUser.socialType(), platformUser.socialId())
                .map(this::generateToken)
                .orElseGet(() -> {
                    String nickname = generateNickname();
                    Member member = Member.builder()
                            .socialType(platformUser.socialType())
                            .socialId(platformUser.socialId())
                            .email(platformUser.email())
                            .nickname(nickname)
                            .build();
                    Member newMember = memberRepository.save(member);
                    return generateToken(newMember);
                });
    }

    private TokenResponse generateToken(Member member) {
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), RoleType.ROLE_회원);
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        saveToken(member.getId(), refreshToken);
        return new TokenResponse(accessToken, refreshToken);
    }

    private void saveToken(Long memberId, String refreshToken) {
        redisTemplate.opsForValue()
                .set(
                        String.valueOf(memberId),
                        refreshToken,
                        jwtTokenProvider.getValidRefreshMilliSecond(),
                        TimeUnit.MILLISECONDS
                );
    }

    private String generateNickname() {
        String nickname;
        do {
            nickname = NicknameGenerator.generate();
        } while (memberRepository.existsByNickname(new Nickname(nickname)));
        return nickname;
    }

    public RenewTokenResponse renewToken(RenewTokenRequest renewTokenRequest) {
        String memberId = jwtTokenProvider.extractSubject(renewTokenRequest.accessToken());
        String savedRefreshToken = (String) redisTemplate.opsForValue().get(memberId);
        validateRefreshToken(savedRefreshToken, renewTokenRequest.refreshToken());

        String accessToken = jwtTokenProvider.createAccessToken(Long.valueOf(memberId), RoleType.ROLE_회원);
        return new RenewTokenResponse(accessToken);
    }

    private void validateRefreshToken(String savedRefreshToken, String refreshToken) {
        JwtTokenValid refreshTokenValid = jwtTokenProvider.validateToken(refreshToken);
        if (!refreshTokenValid.isValid()) {
            throw new AuthenticationException(refreshTokenValid.exceptionType());
        }
        if (!Objects.equals(savedRefreshToken, refreshToken)) {
            throw new AuthenticationException(AuthExceptionType.NOT_MATCH_REFRESH_TOKEN);
        }
    }
}
