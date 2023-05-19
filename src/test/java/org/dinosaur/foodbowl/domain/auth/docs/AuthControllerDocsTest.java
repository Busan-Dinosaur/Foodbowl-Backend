package org.dinosaur.foodbowl.domain.auth.docs;

import static org.dinosaur.foodbowl.global.config.security.jwt.JwtConstant.REFRESH_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.auth.api.AuthController;
import org.dinosaur.foodbowl.domain.auth.application.AuthService;
import org.dinosaur.foodbowl.domain.auth.dto.FoodbowlTokenDto;
import org.dinosaur.foodbowl.domain.auth.dto.request.AppleLoginRequest;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;

@WebMvcTest(AuthController.class)
public class AuthControllerDocsTest extends MockApiTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("애플 로그인을 문서화한다.")
    void appleLogin() throws Exception {
        AppleLoginRequest appleLoginRequest = new AppleLoginRequest("AppleToken");
        FoodbowlTokenDto foodbowlTokenDto = new FoodbowlTokenDto("AccessToken", "RefreshToken");

        given(authService.appleLogin(any())).willReturn(foodbowlTokenDto);

        var requestFieldDescriptors = new FieldDescriptor[]{
                fieldWithPath("appleToken").description("애플에서 발급한 토큰")
        };

        var cookieDescriptors = new CookieDescriptor[]{
                cookieWithName("refreshToken").description("서버에서 발급한 리프레쉬 토큰")
        };

        var responseFieldDescriptors = new FieldDescriptor[]{
                fieldWithPath("accessToken").description("서버에서 발급한 엑세스 토큰")
        };

        mockMvc.perform(post("/api/v1/auth/apple/login")
                        .content(objectMapper.writeValueAsString(appleLoginRequest))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("api-v1-auth-apple-login",
                        requestFields(
                                requestFieldDescriptors
                        ),
                        responseCookies(
                                cookieDescriptors
                        ),
                        responseFields(
                                responseFieldDescriptors
                        )));
    }

    @Test
    @DisplayName("애플 로그아웃을 문서화한다.")
    void appleLogout() throws Exception {
        String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);
        String refreshToken = jwtTokenProvider.createRefreshToken(1L);

        willDoNothing().given(authService).appleLogout(any());

        var requestHeaders = new HeaderDescriptor[]{
                headerWithName("Authorization").description("Foodbowl 인증 토큰")
        };

        var requestCookies = new CookieDescriptor[]{
                cookieWithName("refreshToken").description("Foodbowl 리프레쉬 토큰")
        };

        mockMvc.perform(post("/api/v1/auth/apple/logout")
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(new Cookie(REFRESH_TOKEN.getName(), refreshToken)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("api-v1-auth-apple-logout",
                        requestHeaders(
                                requestHeaders
                        ),
                        requestCookies(
                                requestCookies
                        )));
    }
}
