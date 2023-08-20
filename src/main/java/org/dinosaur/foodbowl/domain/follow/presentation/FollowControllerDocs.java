package org.dinosaur.foodbowl.domain.follow.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.global.exception.ExceptionResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "팔로우", description = "팔로우 API")
public interface FollowControllerDocs {

    @Operation(summary = "팔로우", description = "다른 회원에게 팔로우를 요청한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "팔로우 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "1.본인 팔로우\t\n2.이미 팔로우 되어있는 회원 팔로우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "등록되지 않은 회원 ID 팔로우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> follow(
            @Parameter(description = "팔로우 회원 ID", example = "1") Long targetMemberId,
            Member loginMember
    );

    @Operation(summary = "언팔로우", description = "팔로우 되어있는 회원을 언팔로우한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "언팔로우 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "팔로우하지 않은 회원 언팔로우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "등록되지 않은 회원 ID 언팔로우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> unfollow(
            @Parameter(description = "언팔로우 회원 ID", example = "1") Long targetMemberId,
            Member loginMember
    );
}
