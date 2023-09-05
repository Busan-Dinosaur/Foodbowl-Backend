package org.dinosaur.foodbowl.domain.healthcheck.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.domain.healthcheck.dto.response.HealthCheckResponse;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class HealthCheckServiceTest extends IntegrationTest {

    @Autowired
    private HealthCheckService healthCheckService;

    @Test
    void 헬스_체크를_확인한다() {
        HealthCheckResponse response = healthCheckService.healthCheck();

        assertThat(response.status()).isEqualTo("good");
    }
}
