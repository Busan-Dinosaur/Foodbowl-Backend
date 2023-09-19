package org.dinosaur.foodbowl.domain.blame.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "신고 요청")
public record BlameRequest(
        @Schema(description = "신고 대상 ID", example = "1")
        @Positive(message = "신고 대상 ID는 양의 정수만 가능합니다.")
        Long targetId,

        @Schema(description = "신고 대상 타입", example = "MEMBER")
        @NotNull(message = "신고 대상 타입이 공백이거나 존재하지 않습니다.")
        String blameTarget,

        @Schema(description = "신고 사유", example = "부적절한 리뷰입니다.")
        @NotBlank(message = "신고 사유가 공백이거나 존재하지 않습니다.")
        String description
) {
}
