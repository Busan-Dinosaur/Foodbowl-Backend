package org.dinosaur.foodbowl.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증 토큰 갱신 응답")
public record RenewTokenResponse(
        @Schema(description = "갱신된 인증 토큰", example = "A1B2C3D4")
        String accessToken
) {
}
