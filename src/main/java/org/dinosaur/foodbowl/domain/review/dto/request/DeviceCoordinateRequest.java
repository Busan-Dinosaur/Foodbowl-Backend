package org.dinosaur.foodbowl.domain.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "디바이스 좌표 요청")
public record DeviceCoordinateRequest(
        @Schema(description = "디바이스 경도", example = "123.3636")
        @NotNull(message = "디바이스 경도가 존재하지 않습니다.")
        BigDecimal deviceX,

        @Schema(description = "디바이스 위도", example = "32.3636")
        @NotNull(message = "디바이스 위도가 존재하지 않습니다.")
        BigDecimal deviceY
) {
}
