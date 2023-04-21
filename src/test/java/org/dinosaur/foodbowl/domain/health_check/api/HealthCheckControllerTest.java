package org.dinosaur.foodbowl.domain.health_check.api;

import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.health_check.application.HealthCheckService;
import org.dinosaur.foodbowl.domain.health_check.dto.HealthCheckDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthCheckController.class)
class HealthCheckControllerTest extends MockApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HealthCheckService healthCheckService;

    @DisplayName("헬스 체크 요청 시")
    @Nested
    class Check {

        @DisplayName("정상적인 경우 성공 응답을 반환한다.")
        @Test
        void returnSuccessResponseWhenValid() throws Exception {
            String message = "success";
            LocalDateTime now = LocalDateTime.now();
            HealthCheckDto response = new HealthCheckDto(message, now);

            given(healthCheckService.check()).willReturn(response);

            requestCheckApi()
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(message))
                    .andExpect(jsonPath("$.created").value(now.toString()));
        }

        private ResultActions requestCheckApi() throws Exception {
            return mockMvc.perform(get("/api/v1/health-check"))
                    .andDo(print());
        }
    }
}
