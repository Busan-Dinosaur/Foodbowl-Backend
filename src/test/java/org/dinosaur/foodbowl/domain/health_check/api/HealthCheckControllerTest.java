package org.dinosaur.foodbowl.domain.health_check.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.health_check.application.HealthCheckService;
import org.dinosaur.foodbowl.domain.health_check.dto.HealthCheckDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(HealthCheckController.class)
class HealthCheckControllerTest extends MockApiTest {

    @MockBean
    private HealthCheckService healthCheckService;

    @Nested
    class 헬스_체크_요청시 {

        @Test
        void 정상적인_경우_성공_응답을_반환한다() throws Exception {
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
