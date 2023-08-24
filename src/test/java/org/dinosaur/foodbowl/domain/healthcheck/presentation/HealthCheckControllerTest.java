package org.dinosaur.foodbowl.domain.healthcheck.presentation;

import static org.dinosaur.foodbowl.domain.member.domain.vo.RoleType.ROLE_회원;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.healthcheck.application.HealthCheckService;
import org.dinosaur.foodbowl.domain.healthcheck.dto.response.HealthCheckResponse;
import org.dinosaur.foodbowl.test.PresentationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SuppressWarnings("NonAsciiCharacters")
@WebMvcTest(HealthCheckController.class)
class HealthCheckControllerTest extends PresentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private HealthCheckService healthCheckService;

    @Test
    void 서버_상태가_정상이면_200_반환() throws Exception {
        given(healthCheckService.healthCheck())
                .willReturn(new HealthCheckResponse("good"));

        mockMvc.perform(get("/v1/health-check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("good"))
                .andDo(print());
    }

    @Test
    void 사용자_인증이_정상이면_200_반환() throws Exception {
        mockingAuthMemberInResolver();

        mockMvc.perform(get("/v1/health-check/auth")
                        .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("good: " + member.getNickname()))
                .andDo(print());
    }
}
