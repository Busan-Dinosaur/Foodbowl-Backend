package org.dinosaur.foodbowl.domain.healthcheck.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dinosaur.foodbowl.domain.healthcheck.dto.response.HealthCheckResponse;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.springframework.http.ResponseEntity;

@Tag(name = "헬스 체크", description = "헬스 체크 API")
public interface HealthCheckControllerDocs {

    @Operation(summary = "서버 상태 확인", description = "서버 상태를 확인한다.")
    @ApiResponse(responseCode = "200", description = "서버 상태 확인 성공")
    ResponseEntity<HealthCheckResponse> healthcheck();

    @Operation(summary = "회원 인증 확인", description = "회원 인증 과정을 확인한다.")
    @ApiResponse(responseCode = "200", description = "회원 인증 성공")
    ResponseEntity<HealthCheckResponse> authCheck(Member member);
}
