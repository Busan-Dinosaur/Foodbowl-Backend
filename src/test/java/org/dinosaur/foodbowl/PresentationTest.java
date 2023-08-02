package org.dinosaur.foodbowl;

import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.global.config.SecurityConfig;
import org.dinosaur.foodbowl.global.presentation.jwt.JwtAuthorizationExtractor;
import org.dinosaur.foodbowl.global.presentation.security.CustomAccessDeniedHandler;
import org.dinosaur.foodbowl.global.presentation.security.CustomAuthenticationEntryPoint;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
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
}
