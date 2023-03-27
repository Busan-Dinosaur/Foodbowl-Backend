package org.dinosaur.foodbowl.domain.health_check.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.health_check.dto.HealthCheckDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class HealthCheckServiceTest extends IntegrationTest {

    @Autowired
    private HealthCheckService healthCheckService;

    @Test
    void 헬스_체크_응답을_생성한다() {
        HealthCheckDto result = healthCheckService.check();

        assertThat(result.getMessage()).isEqualTo("success");
    }
}
