package org.dinosaur.foodbowl.domain.healthcheck.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "서버 상태 확인 응답")
public record HealthCheckResponse(
        @Schema(description = "서버 상태", example = "good")
        String status
) {
}
