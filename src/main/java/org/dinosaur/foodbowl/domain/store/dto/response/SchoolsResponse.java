package org.dinosaur.foodbowl.domain.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.dinosaur.foodbowl.domain.store.domain.School;

@Schema(description = "학교 목록 응답")
public record SchoolsResponse(
        @Schema(description = "학교 응답 목록")
        List<SchoolResponse> schools
) {

    public static SchoolsResponse from(List<School> schools) {
        return new SchoolsResponse(listOf(schools));
    }

    private static List<SchoolResponse> listOf(List<School> schools) {
        return schools.stream()
                .map(SchoolResponse::from)
                .toList();
    }
}
