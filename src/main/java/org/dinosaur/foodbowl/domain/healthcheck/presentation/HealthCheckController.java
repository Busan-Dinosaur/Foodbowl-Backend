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
public class HealthCheckController implements HealthCheckControllerDocs {

    private final HealthCheckService healthCheckService;

    @Override
    @GetMapping
    public ResponseEntity<HealthCheckResponse> healthcheck() {
        HealthCheckResponse response = healthCheckService.healthCheck();
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/auth")
    public ResponseEntity<HealthCheckResponse> authCheck(@Auth Member member) {
        return ResponseEntity.ok(new HealthCheckResponse("good: " + member.getNickname()));
    }
}
