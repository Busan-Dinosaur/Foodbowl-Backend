package org.dinosaur.foodbowl.domain.healthcheck.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dinosaur.foodbowl.domain.healthcheck.dto.response.HealthCheckResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "헬스 체크", description = "헬스 체크 API")
public interface HealthCheckController {

    @Operation(summary = "서버 상태 확인", description = "서버 상태를 확인한다.")
    @ApiResponse(responseCode = "200", description = "서버 상태 확인 성공")
    ResponseEntity<HealthCheckResponse> healthcheck();
}
