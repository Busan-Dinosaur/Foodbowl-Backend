package org.dinosaur.foodbowl.domain.store.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dinosaur.foodbowl.domain.store.dto.response.SchoolsResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "학교", description = "학교 API")
public interface SchoolControllerDocs {

    @Operation(
            summary = "학교 목록 조회",
            description = "DB에 존재하는 학교 목록을 조회한다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "학교 목록 조회 성공"
    )
    ResponseEntity<SchoolsResponse> getSchools();
}
