package org.dinosaur.foodbowl.domain.health_check.application;

import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.health_check.dto.HealthCheckDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class HealthCheckServiceTest extends IntegrationTest {

    @Autowired
    private HealthCheckService healthCheckService;

    @DisplayName("헬스 체크 응답을 생성한다.")
    @Test
    void createHealthCheckResponse() {
        HealthCheckDto result = healthCheckService.check();

        assertThat(result.message()).isEqualTo("success");
    }
}
