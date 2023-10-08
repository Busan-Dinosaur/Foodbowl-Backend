package org.dinosaur.foodbowl.domain.auth.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dinosaur.foodbowl.domain.auth.application.AuthService;
import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.auth.dto.reqeust.AppleLoginRequest;
import org.dinosaur.foodbowl.domain.auth.dto.reqeust.RenewTokenRequest;
import org.dinosaur.foodbowl.domain.auth.dto.response.TokenResponse;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.RoleType;
import org.dinosaur.foodbowl.test.PresentationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SuppressWarnings("NonAsciiCharacters")
@WebMvcTest(AuthController.class)
class AuthControllerTest extends PresentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthService authService;

    @Nested
    class 애플_로그인_시 {

        @Test
        void 애플_로그인을_성공하면_토큰과_200_응답을_반환한다() throws Exception {
            AppleLoginRequest appleLoginRequest = new AppleLoginRequest("appleToken");
            TokenResponse tokenResponse = new TokenResponse("accessToken", "refreshToken");
            given(authService.appleLogin(any(AppleLoginRequest.class))).willReturn(tokenResponse);

            mockMvc.perform(post("/v1/auth/login/oauth/apple")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(appleLoginRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("accessToken"))
                    .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 애플_토큰이_공백이거나_존재하지_않으면_400_응답을_반환한다(String appleToken) throws Exception {
            AppleLoginRequest appleLoginRequest = new AppleLoginRequest(appleToken);

            mockMvc.perform(post("/v1/auth/login/oauth/apple")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(appleLoginRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message", containsString("애플 토큰이 존재하지 않습니다.")));
        }
    }

    @Test
    void 로그아웃에_성공하면_204_응답을_반환한다() throws Exception {
        mockingAuthMemberInResolver();
        willDoNothing().given(authService).logout(any(Member.class));

        mockMvc.perform(post("/v1/auth/logout")
                        .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Nested
    class 인증_토큰_갱신_시 {

        @Test
        void 인증_토큰_갱신에_성공하면_인증_토큰과_200_응답을_반환한다() throws Exception {
            RenewTokenRequest request = new RenewTokenRequest("AccessToken", "RefreshToken");
            TokenResponse response = new TokenResponse("RenewAccessToken", "RenewRefreshToken");
            given(authService.renewToken(any(RenewTokenRequest.class))).willReturn(response);

            MvcResult mvcResult = mockMvc.perform(post("/v1/auth/token/renew")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            String jsonResponse = mvcResult.getResponse().getContentAsString();
            TokenResponse result = objectMapper.readValue(jsonResponse, TokenResponse.class);

            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 인증_토큰이_공백이거나_존재하지_않으면_400_응답을_반환한다(String accessToken) throws Exception {
            RenewTokenRequest request = new RenewTokenRequest(accessToken, "RefreshToken");

            mockMvc.perform(post("/v1/auth/token/renew")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message", containsString("인증 토큰이 존재하지 않습니다.")));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 갱신_토큰이_공백이거나_존재하지_않으면_400_응답을_반환한다(String refreshToken) throws Exception {
            RenewTokenRequest request = new RenewTokenRequest("AccessToken", refreshToken);

            mockMvc.perform(post("/v1/auth/token/renew")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message", containsString("갱신 토큰이 존재하지 않습니다.")));
        }
    }
}
