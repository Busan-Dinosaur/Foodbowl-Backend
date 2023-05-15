package org.dinosaur.foodbowl.domain.health_check.docs;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.health_check.api.HealthCheckController;
import org.dinosaur.foodbowl.domain.health_check.application.HealthCheckService;
import org.dinosaur.foodbowl.domain.health_check.dto.HealthCheckDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.FieldDescriptor;

@WebMvcTest(HealthCheckController.class)
class HealthCheckControllerDocsTest extends MockApiTest {

    @MockBean
    private HealthCheckService healthCheckService;

    @Test
    @DisplayName("헬스 체크")
    void healthCheck() throws Exception {
        String message = "success";
        LocalDateTime now = LocalDateTime.now();
        HealthCheckDto response = new HealthCheckDto(message, now);

        given(healthCheckService.check()).willReturn(response);

        var responseFieldDescriptors = new FieldDescriptor[]{
                fieldWithPath("message").description("헬스 체크 메시지"),
                fieldWithPath("created").description("헬스 체크 전송 시간")
        };

        mockMvc.perform(get("/api/v1/health-check"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("api-v1-health-check",
                        responseFields(
                                responseFieldDescriptors
                        )));
    }
}
