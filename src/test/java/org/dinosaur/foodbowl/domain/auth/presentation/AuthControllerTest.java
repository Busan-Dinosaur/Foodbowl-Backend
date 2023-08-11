package org.dinosaur.foodbowl.domain.auth.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dinosaur.foodbowl.domain.auth.application.AuthService;
import org.dinosaur.foodbowl.domain.auth.dto.reqeust.AppleLoginRequest;
import org.dinosaur.foodbowl.domain.auth.dto.response.TokenResponse;
import org.dinosaur.foodbowl.test.PresentationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SuppressWarnings("NonAsciiCharacters")
@WebMvcTest(AuthController.class)
class AuthControllerTest extends PresentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Nested
    class 애플_로그인 {

        @NullAndEmptySource
        @ParameterizedTest
        void 애플_토큰이_공백이거나_존재하지_않으면_400_응답을_반환한다(String appleToken) throws Exception {
            AppleLoginRequest appleLoginRequest = new AppleLoginRequest(appleToken);

            mockMvc.perform(post("/v1/auth/login/oauth/apple")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(appleLoginRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value("애플 토큰이 존재하지 않습니다."));
        }

        @Test
        void 애플_로그인을_수행하면_토큰과_200_응답을_반환한다() throws Exception {
            AppleLoginRequest appleLoginRequest = new AppleLoginRequest("appleToken");
            TokenResponse tokenResponse = new TokenResponse("accessToken", "refreshToken");
            BDDMockito.given(authService.appleLogin(any(AppleLoginRequest.class))).willReturn(tokenResponse);

            mockMvc.perform(post("/v1/auth/login/oauth/apple")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(appleLoginRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("accessToken"))
                    .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
        }
    }
}
