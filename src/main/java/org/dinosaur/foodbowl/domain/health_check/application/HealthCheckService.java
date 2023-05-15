package org.dinosaur.foodbowl.domain.health_check.application;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.health_check.dto.HealthCheckDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class HealthCheckService {

    public HealthCheckDto check() {
        return new HealthCheckDto("success", LocalDateTime.now());
    }
}
