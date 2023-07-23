package org.dinosaur.foodbowl;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.dinosaur.foodbowl.global.config.SecurityConfig;
import org.dinosaur.foodbowl.global.config.security.CustomAccessDeniedHandler;
import org.dinosaur.foodbowl.global.config.security.CustomAuthenticationEntryPoint;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtAuthenticationFilter;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtAuthorizationExtractor;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@Import(value = {
        SecurityConfig.class,
        JwtTokenProvider.class,
        JwtAuthorizationExtractor.class,
        JwtAuthenticationFilter.class,
        CustomAccessDeniedHandler.class,
        CustomAuthenticationEntryPoint.class,
})
@ExtendWith({SpringExtension.class})
public class MockApiTest {

    protected MockMvc mockMvc;

    @BeforeEach
    protected void setUpMockMvc(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(springSecurity())
                .build();
    }
}
