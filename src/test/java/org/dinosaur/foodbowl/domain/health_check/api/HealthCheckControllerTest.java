package org.dinosaur.foodbowl.domain.health_check.api;

import com.jayway.jsonpath.JsonPath;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.health_check.application.HealthCheckService;
import org.dinosaur.foodbowl.domain.health_check.dto.HealthCheckDto;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthCheckController.class)
class HealthCheckControllerTest extends MockApiTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private HealthCheckService healthCheckService;

    private ResultMatcher jsonPathLocalDateTimeEquals(String expression, LocalDateTime expectedLocalDateTime) {
        return result -> {
            String content = result.getResponse().getContentAsString();
            String localDateTimeString = JsonPath.read(content, expression);
            LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            assertEquals(localDateTime, expectedLocalDateTime);
        };
    }

    @Nested
    @DisplayName("인증 없는 헬스 체크 요청 시")
    class Check {

        @Test
        @DisplayName("정상적인 경우 성공 응답을 반환한다.")
        void returnSuccessResponseWhenValid() throws Exception {
            String message = "success";
            LocalDateTime now = LocalDateTime.now();
            HealthCheckDto response = new HealthCheckDto(message, now);

            given(healthCheckService.check()).willReturn(response);

            requestCheckApi()
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(message))
                    .andExpect(jsonPathLocalDateTimeEquals("$.created", now));
        }

        private ResultActions requestCheckApi() throws Exception {
            return mockMvc.perform(get("/api/v1/health-check"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("인증 있는 헬스 체크 요청 시")
    class AuthCheck {

        @Test
        @DisplayName("유효하지 않은 토큰인 경우 401 예외를 던진다.")
        void throwExceptionNotExistToken() throws Exception {
            String message = "success";
            LocalDateTime now = LocalDateTime.now();
            HealthCheckDto response = new HealthCheckDto(message, now);

            given(healthCheckService.check()).willReturn(response);

            requestAuthCheckApi("invalid-token")
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("인증 토큰 권한이 맞지 않는 경우 403 예외를 던진다")
        void throwExceptionNotMatchAuthority() throws Exception {
            String message = "success";
            LocalDateTime now = LocalDateTime.now();
            HealthCheckDto response = new HealthCheckDto(message, now);

            String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_관리자);

            given(healthCheckService.check()).willReturn(response);

            requestAuthCheckApi(accessToken)
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("인증 토큰이 있는 경우 성공 응답을 반환한다.")
        void returnSuccessResponseWhenExistToken() throws Exception {
            String message = "success";
            LocalDateTime now = LocalDateTime.now();
            HealthCheckDto response = new HealthCheckDto(message, now);

            String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

            given(healthCheckService.check()).willReturn(response);

            requestAuthCheckApi(accessToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(message))
                    .andExpect(jsonPathLocalDateTimeEquals("$.created", now));
        }

        private ResultActions requestAuthCheckApi(String token) throws Exception {
            return mockMvc.perform(get("/api/v1/health-check/auth")
                            .header("Authorization", "Bearer " + token))
                    .andDo(print());
        }
    }
}
