package org.dinosaur.foodbowl.test;


import org.dinosaur.foodbowl.domain.auth.jwt.JwtAuthorizationExtractor;
import org.dinosaur.foodbowl.domain.auth.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.dinosaur.foodbowl.global.config.SecurityConfig;
import org.dinosaur.foodbowl.global.config.security.CustomAccessDeniedHandler;
import org.dinosaur.foodbowl.global.config.security.CustomAuthenticationEntryPoint;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import(value = {
        SecurityConfig.class,
        JwtTokenProvider.class,
        JwtAuthorizationExtractor.class,
        CustomAccessDeniedHandler.class,
        CustomAuthenticationEntryPoint.class
})
public class PresentationTest {

    @MockBean
    protected MemberRepository memberRepository;
}
