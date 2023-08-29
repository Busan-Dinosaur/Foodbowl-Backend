package org.dinosaur.foodbowl.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "닉네임 존재 여부 응답")
public record NicknameExistResponse(
        @Schema(description = "닉네임 존재 여부", example = "true")
        boolean isExist
) {
}
