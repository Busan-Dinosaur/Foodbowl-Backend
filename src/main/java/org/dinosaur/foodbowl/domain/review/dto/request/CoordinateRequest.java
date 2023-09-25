package org.dinosaur.foodbowl.domain.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "좌표 범위 요청")
public record CoordinateRequest(
        @Schema(description = "경도", example = "123.3636")
        @NotNull(message = "경도값이 존재하지 않습니다.")
        BigDecimal x,

        @Schema(description = "위도", example = "32.3636")
        @NotNull(message = "위도값이 존재하지 않습니다.")
        BigDecimal y,

        @Schema(description = "경도 증가값", example = "3.1616")
        @NotNull(message = "경도 증가값이 존재하지 않습니다.")
        BigDecimal deltaX,

        @Schema(description = "위도 증가값", example = "3.1616")
        @NotNull(message = "위도 증가값이 존재하지 않습니다.")
        BigDecimal deltaY
) {
}
