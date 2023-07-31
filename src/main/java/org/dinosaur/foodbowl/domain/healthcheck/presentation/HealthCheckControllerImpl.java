package org.dinosaur.foodbowl.domain.healthcheck.presentation;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.healthcheck.application.HealthCheckService;
import org.dinosaur.foodbowl.domain.healthcheck.dto.response.HealthCheckResponse;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.global.presentation.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/health-check")
@RestController
public class HealthCheckControllerImpl implements HealthCheckController {

    private final HealthCheckService healthCheckService;

    @GetMapping
    public ResponseEntity<HealthCheckResponse> healthCheck() {
        HealthCheckResponse response = healthCheckService.healthCheck();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth")
    public ResponseEntity<HealthCheckResponse> authCheck(@Auth Member member) {
        HealthCheckResponse response = healthCheckService.healthCheck();
        return ResponseEntity.ok(response);
    }
}
