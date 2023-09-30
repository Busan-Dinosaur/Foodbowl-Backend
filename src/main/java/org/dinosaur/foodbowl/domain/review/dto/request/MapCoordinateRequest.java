package org.dinosaur.foodbowl.domain.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Schema(description = "지도 좌표 요청")
public record MapCoordinateRequest(
        @Schema(description = "지도 중심 경도", example = "123.3636")
        @NotNull(message = "지도 중심 경도가 존재하지 않습니다.")
        BigDecimal x,

        @Schema(description = "지도 중심 위도", example = "32.3636")
        @NotNull(message = "지도 중심 위도가 존재하지 않습니다.")
        BigDecimal y,

        @Schema(description = "지도 중심 경도로부터 경도 증가값", example = "3.1616")
        @NotNull(message = "경도 증가값이 존재하지 않습니다.")
        @Positive(message = "경도 증가값은 0이상의 양수만 가능합니다.")
        BigDecimal deltaX,

        @Schema(description = "지도 중심 위도로부터 위도 증가값", example = "3.1616")
        @NotNull(message = "위도 증가값이 존재하지 않습니다.")
        @Positive(message = "위도 증가값은 0이상의 양수만 가능합니다.")
        BigDecimal deltaY
) {
}
