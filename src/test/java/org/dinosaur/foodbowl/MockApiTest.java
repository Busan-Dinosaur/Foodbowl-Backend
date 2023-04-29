package org.dinosaur.foodbowl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dinosaur.foodbowl.global.config.security.CustomAccessDeniedHandler;
import org.dinosaur.foodbowl.global.config.security.CustomAuthenticationEntryPoint;
import org.dinosaur.foodbowl.global.config.security.SecurityConfig;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtAuthenticationFilter;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtAuthorizationExtractor;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Import(value = {
        SecurityConfig.class,
        JwtTokenProvider.class,
        JwtAuthorizationExtractor.class,
        JwtAuthenticationFilter.class,
        CustomAccessDeniedHandler.class,
        CustomAuthenticationEntryPoint.class,
})
@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
public class MockApiTest {

    protected ObjectMapper objectMapper = new ObjectMapper();
    protected MockMvc mockMvc;

    @BeforeEach
    protected void setUpMockMvc(
            WebApplicationContext webApplicationContext,
            RestDocumentationContextProvider restDocumentationContextProvider
    ) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(springSecurity())
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(
                                modifyUris().scheme("https").host("docs.api.com").removePort(), prettyPrint())
                        .withResponseDefaults(prettyPrint())
                )
                .build();
    }
}
