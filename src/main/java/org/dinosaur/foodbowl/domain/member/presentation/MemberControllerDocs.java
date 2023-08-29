package org.dinosaur.foodbowl.domain.member.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.PositiveOrZero;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileResponse;
import org.dinosaur.foodbowl.global.exception.ExceptionResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "회원", description = "회원 API")
public interface MemberControllerDocs {

    @Operation(summary = "회원 프로필 조회", description = "회원 프로필을 조회한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "회원 프로필 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.올바르지 않은 회원 ID 타입
                                                        
                            2.양수가 아닌 회원 ID
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "등록되지 않은 회원 ID",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<MemberProfileResponse> getProfile(
            @Parameter(description = "회원 ID", example = "1")
            @PositiveOrZero(message = "회원 ID는 양수만 가능합니다.")
            Long memberId,

            Member loginMember
    );
}
