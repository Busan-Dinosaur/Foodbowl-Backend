package org.dinosaur.foodbowl.domain.auth.api;

import static org.dinosaur.foodbowl.global.config.security.jwt.JwtConstant.REFRESH_TOKEN;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.auth.application.AuthService;
import org.dinosaur.foodbowl.domain.auth.dto.FoodbowlTokenDto;
import org.dinosaur.foodbowl.domain.auth.dto.request.AppleLoginRequest;
import org.dinosaur.foodbowl.domain.auth.dto.response.NicknameDuplicateCheckResponse;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends MockApiTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Nested
    @DisplayName("애플 로그인 api는 ")
    class AppleLogin {

        @Test
        @DisplayName("유효한 애플 토큰 값을 요청 받으면 Foodbowl 토큰을 응답한다.")
        void appleLogin() throws Exception {
            AppleLoginRequest appleLoginRequest = new AppleLoginRequest("AppleToken");
            FoodbowlTokenDto foodbowlTokenDto = new FoodbowlTokenDto("AccessToken", "RefreshToken");

            given(authService.appleLogin(any())).willReturn(foodbowlTokenDto);

            appleLoginApi(appleLoginRequest)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("AccessToken"));
        }

        @Test
        @DisplayName("토큰이 존재하지 않으면 400 예외를 발생시킨다.")
        void appleLoginWithEmptyToken() throws Exception {
            AppleLoginRequest appleLoginRequest = new AppleLoginRequest(null);

            appleLoginApi(appleLoginRequest)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("애플 토큰이 필요합니다.")))
                    .andExpect(jsonPath("$.code").value(-1000));
        }

        private ResultActions appleLoginApi(AppleLoginRequest request) throws Exception {
            return mockMvc.perform(post("/api/v1/auth/apple/login")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("애플 로그아웃 api는 ")
    class AppleLogout {

        @Test
        @DisplayName("성공 시 리프레쉬 토큰을 삭제한다.")
        void appleLogout() throws Exception {
            String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

            willDoNothing().given(authService).appleLogout(any());

            appleLogoutApi(accessToken)
                    .andExpect(status().isNoContent())
                    .andExpect(cookie().exists("refreshToken"))
                    .andExpect(cookie().value("refreshToken", nullValue()))
                    .andExpect(cookie().maxAge("refreshToken", 0));
        }

        @Test
        @DisplayName("인증 토큰이 존재하지 않으면 401 예외를 발생시킨다.")
        void appleLogoutWithEmptyToken() throws Exception {
            appleLogoutApi(null)
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("인증에 실패하였습니다."));
        }

        private ResultActions appleLogoutApi(String accessToken) throws Exception {
            return mockMvc.perform(post("/api/v1/auth/apple/logout")
                            .header("Authorization", "Bearer " + accessToken)
                            .cookie(new Cookie(REFRESH_TOKEN.getName(), "refreshToken")))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("닉네임 중복 검증은")
    class CheckDuplicate {

        @Test
        @DisplayName("요청 파라미터가 없으면 BAD REQUEST가 발생한다.")
        void checkDuplicateFailWithNoParams() throws Exception {
            given(authService.checkDuplicate(any())).willReturn(new NicknameDuplicateCheckResponse(false));

            mockMvc.perform(get("/api/v1/auth/check-nickname")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("요청 닉네임이 존재하면 true를 반환한다.")
        void checkDuplicateTrue() throws Exception {
            given(authService.checkDuplicate(any())).willReturn(new NicknameDuplicateCheckResponse(true));

            mockMvc.perform(get("/api/v1/auth/check-nickname")
                            .queryParam("nickname", "gray")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.hasDuplicate").value(true))
                    .andDo(print());
        }

        @Test
        @DisplayName("요청 닉네임이 존재하지 않으면 false를 반환한다.")
        void checkDuplicateFalse() throws Exception {
            given(authService.checkDuplicate(any())).willReturn(new NicknameDuplicateCheckResponse(false));

            mockMvc.perform(get("/api/v1/auth/check-nickname")
                            .queryParam("nickname", "gray")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.hasDuplicate").value(false))
                    .andDo(print());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "graygraygraygrayhoy", "@!!dsafdsf$"})
        @DisplayName("닉네임은 1자 이상 16자 이하 한글,영문,숫자가 아니면 BAD REQUEST가 발생한다.")
        void checkDuplicateFail(String nickname) throws Exception {
            given(authService.checkDuplicate(any())).willReturn(new NicknameDuplicateCheckResponse(false));

            mockMvc.perform(get("/api/v1/auth/check-nickname")
                            .queryParam("nickname", nickname)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("닉네임은 1자 이상 16자 이하 한글, 영문, 숫자만 가능합니다"))
                    .andDo(print());
        }
    }
}
