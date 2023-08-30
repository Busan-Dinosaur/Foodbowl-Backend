package org.dinosaur.foodbowl.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "프로필 수정 요청")
public record UpdateProfileRequest(
        @Schema(description = "닉네임", example = "coby5502")
        @NotBlank(message = "닉네임이 공백이거나 존재하지 않습니다.")
        String nickname,

        @Schema(description = "한 줄 소개", example = "동네 맛집 탐험을 좋아하는 아저씨에요.")
        @NotBlank(message = "한 줄 소개가 공백이거나 존재하지 않습니다.")
        String introduction
) {
}
