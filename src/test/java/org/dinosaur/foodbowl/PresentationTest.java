package org.dinosaur.foodbowl;

import org.dinosaur.foodbowl.domain.auth.jwt.JwtAuthorizationExtractor;
import org.dinosaur.foodbowl.domain.auth.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.global.config.SecurityConfig;
import org.dinosaur.foodbowl.global.config.security.CustomAccessDeniedHandler;
import org.dinosaur.foodbowl.global.config.security.CustomAuthenticationEntryPoint;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.context.annotation.Import;

@Import(value = {
        SecurityConfig.class,
        JwtTokenProvider.class,
        JwtAuthorizationExtractor.class,
        CustomAccessDeniedHandler.class,
        CustomAuthenticationEntryPoint.class
})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PresentationTest {
}
