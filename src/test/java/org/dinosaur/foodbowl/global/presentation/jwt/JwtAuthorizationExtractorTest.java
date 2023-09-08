package org.dinosaur.foodbowl.global.presentation.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JwtAuthorizationExtractorTest {

    private static final JwtAuthorizationExtractor jwtAuthorizationExtractor = new JwtAuthorizationExtractor();

    @Nested
    class 인증_토큰_추출_시 {

        @Test
        void 인증_헤더가_존재하면_인증_토큰을_반환한다() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "Bearer Token");

            Optional<String> result = jwtAuthorizationExtractor.extractAccessToken(request);

            assertThat(result).isPresent().contains("Token");
        }

        @Test
        void 인증_헤더가_존재하지_않으면_빈값을_반환한다() {
            MockHttpServletRequest request = new MockHttpServletRequest();

            Optional<String> result = jwtAuthorizationExtractor.extractAccessToken(request);

            assertThat(result).isEmpty();
        }

        @Test
        void bearer방식이_아니라면_빈값을_반환한다() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "Basic token");

            Optional<String> result = jwtAuthorizationExtractor.extractAccessToken(request);

            assertThat(result).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {"Bearer", "Bearer "})
        void 인증_토큰이_존재하지_않으면_빈값을_반환한다(String auth) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", auth);

            Optional<String> result = jwtAuthorizationExtractor.extractAccessToken(request);

            assertThat(result).isEmpty();
        }
    }
}
