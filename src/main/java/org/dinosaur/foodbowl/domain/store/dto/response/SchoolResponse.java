package org.dinosaur.foodbowl.domain.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import org.dinosaur.foodbowl.domain.store.domain.School;

@Schema(description = "학교 응답")
public record SchoolResponse(
        @Schema(description = "학교 ID", example = "1")
        Long id,

        @Schema(description = "학교명", example = "부산대학교")
        String name,

        @Schema(description = "경도", example = "127.521")
        BigDecimal x,

        @Schema(description = "위도", example = "35.77")
        BigDecimal y
) {

    public static SchoolResponse from(School school) {
        return new SchoolResponse(
                school.getId(),
                school.getName(),
                school.getX(),
                school.getY()
        );
    }
}
