package org.dinosaur.foodbowl.domain.follow.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.dinosaur.foodbowl.domain.follow.dto.response.FollowerResponse;
import org.dinosaur.foodbowl.domain.follow.dto.response.FollowingResponse;
import org.dinosaur.foodbowl.domain.follow.dto.response.OtherUserFollowerResponse;
import org.dinosaur.foodbowl.domain.follow.dto.response.OtherUserFollowingResponse;
import org.dinosaur.foodbowl.global.common.response.PageResponse;
import org.dinosaur.foodbowl.global.exception.response.ExceptionResponse;
import org.dinosaur.foodbowl.global.presentation.LoginMember;
import org.springframework.http.ResponseEntity;

@Tag(name = "팔로우", description = "팔로우 API")
public interface FollowControllerDocs {

    @Operation(summary = "팔로잉 목록 조회", description = "정해진 개수만큼 팔로잉 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "팔로잉 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.페이지 타입이 올바르지 않은 경우
                                                        
                            2.음수 페이지인 경우
                                                        
                            3.페이지 크기 타입이 올바르지 않은 경우
                                                        
                            4.페이지 크기가 음수인 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<PageResponse<FollowingResponse>> getFollowings(
            @Parameter(description = "페이지", example = "0")
            @PositiveOrZero(message = "페이지는 0이상만 가능합니다.")
            int page,

            @Parameter(description = "페이지 크기", example = "15")
            @PositiveOrZero(message = "페이지 크기는 0이상만 가능합니다.")
            int size,

            LoginMember loginMember
    );

    @Operation(summary = "다른 회원 팔로잉 목록 조회", description = "정해진 개수만큼 다른 회원의 팔로잉 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "다른 회원 팔로잉 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.회원 ID 타입이 올바르지 않은 경우
                                                        
                            2.회원 ID가 양수가 아닌 경우
                                                        
                            3.페이지 타입이 올바르지 않은 경우
                                                        
                            4.페이지가 음수인 경우
                                                        
                            5.페이지 크기 타입이 올바르지 않은 경우
                                                        
                            6.페이지 크기가 음수인 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "다른 회원 ID가 등록되지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
    })
    ResponseEntity<PageResponse<OtherUserFollowingResponse>> getOtherUserFollowings(
            @Parameter(description = "다른 회원 ID", example = "1")
            @Positive(message = "ID는 양수만 가능합니다.")
            Long targetMemberId,

            @Parameter(description = "페이지", example = "0")
            @PositiveOrZero(message = "페이지는 0이상만 가능합니다.")
            int page,

            @Parameter(description = "페이지 크기", example = "15")
            @PositiveOrZero(message = "페이지 크기는 0이상만 가능합니다.")
            int size,

            LoginMember loginMember
    );

    @Operation(summary = "팔로워 목록 조회", description = "정해진 개수만큼 팔로워 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "팔로워 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.페이지 타입이 올바르지 않은 경우
                                                        
                            2.페이지가 음수인 경우
                                                        
                            3.페이지 크기 타입이 올바르지 않은 경우
                                                        
                            4.페이지 크기가 음수인 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<PageResponse<FollowerResponse>> getFollowers(
            @Parameter(description = "페이지", example = "0")
            @PositiveOrZero(message = "페이지는 0이상만 가능합니다.")
            int page,

            @Parameter(description = "페이지 크기", example = "15")
            @PositiveOrZero(message = "페이지 크기는 0이상만 가능합니다.")
            int size,

            LoginMember loginMember
    );

    @Operation(summary = "다른 회원 팔로워 목록 조회", description = "정해진 개수만큼 다른 회원의 팔로워 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "다른 회원 팔로워 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.회원 ID 타입이 올바르지 않은 경우
                                                        
                            2.회원 ID가 양수가 아닌 경우
                                                        
                            3.페이지 타입이 올바르지 않은 경우
                                                        
                            4.페이지가 음수인 경우
                                                        
                            5.페이지 크기 타입이 올바르지 않은 경우
                                                        
                            6.페이지 크기가 음수인 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "다른 회원 ID가 등록되지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
    })
    ResponseEntity<PageResponse<OtherUserFollowerResponse>> getOtherUserFollowers(
            @Parameter(description = "다른 회원 ID", example = "1")
            @Positive(message = "ID는 양수만 가능합니다.")
            Long targetMemberId,

            @Parameter(description = "페이지", example = "0")
            @PositiveOrZero(message = "페이지는 0이상만 가능합니다.")
            int page,

            @Parameter(description = "페이지 크기", example = "15")
            @PositiveOrZero(message = "페이지 크기는 0이상만 가능합니다.")
            int size,

            LoginMember loginMember
    );

    @Operation(summary = "팔로우", description = "다른 회원에게 팔로우를 요청한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "팔로우 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.회원 ID 타입이 올바르지 않은 경우
                                                        
                            2.회원 ID가 양수가 아닌 경우
                                                        
                            3.스스로를 팔로우하는 경우
                                                        
                            4.이미 팔로우 되어있는 회원을 팔로우하는 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "팔로우 대상 회원 ID가 등록되지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> follow(
            @Parameter(description = "팔로우 대상 회원 ID", example = "1")
            @Positive(message = "ID는 양수만 가능합니다.")
            Long targetMemberId,

            LoginMember loginMember
    );

    @Operation(summary = "언팔로우", description = "팔로우 되어있는 회원을 언팔로우한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "언팔로우 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.회원 ID 타입이 올바르지 않은 경우
                                                        
                            2.회원 ID가 양수가 아닌 경우
                                                        
                            3.팔로우하지 않은 회원을 언팔로우하는 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "언팔로우 회원 ID가 등록되지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> unfollow(
            @Parameter(description = "언팔로우 회원 ID", example = "1")
            @Positive(message = "ID는 양수만 가능합니다.")
            Long targetMemberId,

            LoginMember loginMember
    );

    @Operation(summary = "팔로워 삭제", description = "나를 팔로워 중인 회원의 팔로우를 삭제한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "팔로워 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.회원 ID 타입이 올바르지 않은 경우
                                                        
                            2.회원 ID가 양수가 아닌 경우
                                                        
                            3.나를 팔로우 하지 않은 회원을 삭제하는 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "팔로워 삭제 회원 ID가 등록되지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> deleteFollower(
            @Parameter(description = "팔로워 삭제 회원 ID", example = "1")
            @Positive(message = "ID는 양수만 가능합니다.")
            Long targetMemberId,

            LoginMember loginMember
    );
}
