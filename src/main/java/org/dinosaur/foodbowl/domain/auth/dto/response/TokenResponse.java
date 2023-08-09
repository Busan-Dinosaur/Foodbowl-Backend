package org.dinosaur.foodbowl.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 응답")
public record TokenResponse(
        @Schema(description = "인증 토큰", example = "a1b2c3d4")
        String accessToken,
        @Schema(description = "갱신 토큰", example = "a1b2c3d4")
        String refreshToken
) {
}
