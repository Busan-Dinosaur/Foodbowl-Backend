package org.dinosaur.foodbowl.domain.health_check.api;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.health_check.application.HealthCheckService;
import org.dinosaur.foodbowl.domain.health_check.dto.HealthCheckDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HealthCheckController {

    private final HealthCheckService healthCheckService;

    @GetMapping("/health-check")
    public ResponseEntity<HealthCheckDto> check() {
        HealthCheckDto response = healthCheckService.check();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health-check/auth")
    public ResponseEntity<HealthCheckDto> authCheck() {
        HealthCheckDto response = healthCheckService.check();

        return ResponseEntity.ok(response);
    }
}
