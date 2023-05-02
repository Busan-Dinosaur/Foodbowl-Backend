package org.dinosaur.foodbowl.domain.auth.api;

import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.auth.application.AuthService;
import org.dinosaur.foodbowl.domain.auth.dto.FoodbowlTokenDto;
import org.dinosaur.foodbowl.domain.auth.dto.request.AppleLoginRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends MockApiTest {

    @MockBean
    private AuthService authService;

    @Nested
    @DisplayName("애플 로그인 api는 ")
    class AppleLogin {

        @Test
        @DisplayName("유효한 애플 토큰 값을 요청 받으면 Foodbowl 토큰을 응답한다.")
        void appleLogin() throws Exception {
            AppleLoginRequestDto appleLoginRequestDto = new AppleLoginRequestDto("AppleToken");
            FoodbowlTokenDto foodbowlTokenDto = new FoodbowlTokenDto("AccessToken", "RefreshToken");

            given(authService.appleLogin(any())).willReturn(foodbowlTokenDto);

            appleLoginApi(appleLoginRequestDto)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("AccessToken"));
        }

        @Test
        @DisplayName("토큰이 존재하지 않으면 400 예외를 발생시킨다.")
        void appleLoginWithEmptyToken() throws Exception {
            AppleLoginRequestDto appleLoginRequestDto = new AppleLoginRequestDto(null);

            appleLoginApi(appleLoginRequestDto)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("애플 토큰이 필요합니다.")))
                    .andExpect(jsonPath("$.code").value(-1000));
        }

        private ResultActions appleLoginApi(AppleLoginRequestDto request) throws Exception {
            return mockMvc.perform(post("/api/v1/auth/apple/login")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print());
        }
    }
}
