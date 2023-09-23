package org.dinosaur.foodbowl.domain.auth.dto.reqeust;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "인증 토큰 갱신 요청")
public record RenewTokenRequest(
        @Schema(description = "만료된 인증 토큰", example = "A1B2C3D3")
        @NotBlank(message = "인증 토큰이 존재하지 않습니다.")
        String accessToken,

        @Schema(description = "갱신 토큰", example = "A1B2C3D3")
        @NotBlank(message = "갱신 토큰이 존재하지 않습니다.")
        String refreshToken
) {
}
