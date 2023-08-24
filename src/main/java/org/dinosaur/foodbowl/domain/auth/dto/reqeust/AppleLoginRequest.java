package org.dinosaur.foodbowl.domain.auth.dto.reqeust;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "애플 로그인 요청")
public record AppleLoginRequest(
        @Schema(description = "애플 토큰", example = "a1b2c3d4", requiredMode = REQUIRED)
        @NotBlank(message = "애플 토큰이 존재하지 않습니다.")
        String appleToken
) {
}
