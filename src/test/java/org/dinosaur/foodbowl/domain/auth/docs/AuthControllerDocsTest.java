package org.dinosaur.foodbowl.domain.auth.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.auth.api.AuthController;
import org.dinosaur.foodbowl.domain.auth.application.AuthService;
import org.dinosaur.foodbowl.domain.auth.dto.FoodbowlTokenDto;
import org.dinosaur.foodbowl.domain.auth.dto.request.AppleLoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;

@WebMvcTest(AuthController.class)
public class AuthControllerDocsTest extends MockApiTest {

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
                .andDo(document("api-v1-apple-login",
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
}
