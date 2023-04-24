package org.dinosaur.foodbowl.domain.health_check.application;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.health_check.dto.HealthCheckDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HealthCheckService {

    public HealthCheckDto check() {
        return new HealthCheckDto("success", LocalDateTime.now());
    }
}
