package org.dinosaur.foodbowl.domain.healthcheck.application;

import org.dinosaur.foodbowl.domain.healthcheck.dto.response.HealthCheckResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HealthCheckService {

    @Transactional
    public HealthCheckResponse healthCheck() {
        return new HealthCheckResponse("good");
    }
}
