package org.dinosaur.foodbowl.test;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.dinosaur.foodbowl.global.config.SecurityConfig;
import org.dinosaur.foodbowl.global.presentation.jwt.JwtAuthorizationExtractor;
import org.dinosaur.foodbowl.global.presentation.security.CustomAccessDeniedHandler;
import org.dinosaur.foodbowl.global.presentation.security.CustomAuthenticationEntryPoint;
import org.dinosaur.foodbowl.test.config.TestPropertyConfig;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import(value = {
        SecurityConfig.class,
        TestPropertyConfig.class,
        JwtTokenProvider.class,
        JwtAuthorizationExtractor.class,
        CustomAccessDeniedHandler.class,
        CustomAuthenticationEntryPoint.class
})
public class PresentationTest {

    protected static final String AUTHORIZATION = "Authorization";
    protected static final String BEARER = "Bearer ";
    protected static final Member member = Member.builder()
            .email("foodBowl@gmail.com")
            .socialId("foodBowlId")
            .socialType(SocialType.APPLE)
            .nickname("foodbowl")
            .introduction("푸드볼 서비스")
            .build();

    @MockBean
    protected MemberRepository memberRepository;

    protected void mockingAuthMemberInResolver() {
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));
    }
}
