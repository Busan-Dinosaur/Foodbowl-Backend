package org.dinosaur.foodbowl.domain.review.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.dto.request.CoordinateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewUpdateRequest;
import org.dinosaur.foodbowl.domain.review.dto.response.PaginationReviewResponse;
import org.dinosaur.foodbowl.global.exception.response.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "리뷰", description = "리뷰 API")
public interface ReviewControllerDocs {

    @Operation(
            summary = "팔로잉 멤버의 리뷰 목록 범위 기반 페이징 조회",
            description = """
                    지도 중심의 경도(x), 위도(y)와 경도 증가값(deltaX), 위도 증가값(deltaY)을 통해 사각형 범위를 생성하여
                                        
                    해당 범위에 속한 팔로잉 하고 있는 멤버들이 작성한 리뷰 목록을 조회하는 기능입니다.
                                        
                    조회 성능을 높이기 위해 NO OFFSET 페이징으로 구현하였기에 페이지 번호 대신 마지막 리뷰 ID를 요청값으로 받습니다.
                                        
                    첫 페이지 조회 시에는 마지막 리뷰 ID를 파라미터에 담지 않고 요청을 보내면 됩니다.
                                        
                    두번째 페이지 조회 부터는 이전 조회 응답에 담겨 있는 마지막 리뷰 ID를 파라미터로 넘겨주면
                                        
                    해당 리뷰 ID보다 작은, 다시 말해서 요청으로 보낸 리뷰 이전에 작성된 리뷰부터 조회하게 됩니다.
                                        
                    페이지 크기(응답할 리뷰 개수)는 파라미터로 보내지 않으면 기본적으로 10개로 동작하게 되어있습니다. 
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "팔로잉 멤버의 리뷰 목록 범위 기반 페이징 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.리뷰 ID가 양수가 아닌 경우
                                                        
                            2.경도가 존재하지 않은 경우
                                                        
                            3.위도가 존재하지 않은 경우
                                                        
                            4.경도 증가값이 존재하지 않은 경우
                                                        
                            5.위도 증가값이 존재하지 않은 경우
                                                        
                            6.페이지 크기가 양수가 아닌 경우
                            """
            )
    })
    ResponseEntity<PaginationReviewResponse> getPaginationReviewsByFollowing(
            @Positive(message = "리뷰 ID는 양수만 가능합니다.") Long lastReviewId,
            @Valid CoordinateRequest coordinateRequest,
            @Positive(message = "페이지 크기는 양수만 가능합니다.") int pageSize,
            Member loginMember
    );

    @Operation(summary = "리뷰 등록", description = "가게에 해당하는 리뷰를 등록합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "리뷰 등록 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.사진이 4개보다 많은 경우
                                                        
                            2.가게 이름 값이 없는 경우
                                                        
                            3.가게 주소 필드가 값이 없는 경우
                                                        
                            4.가게 경도 값이 없는 경우
                                                        
                            5.가게 위도 값이 없는 경우
                                                        
                            6.가게 정보 URL이 없는 경우
                                                        
                            7.카테고리 정보가 없는 경우
                                                        
                            8.가게 리뷰 내용이 없는 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> create(
            @Valid ReviewCreateRequest reviewCreateRequest,
            @Size(max = 4, message = "사진의 개수는 최대 4개까지 가능합니다.") List<MultipartFile> imageFiles,
            Member member
    );

    @Operation(summary = "리뷰 수정",
            description = """
                    가게에 해당하는 리뷰를 수정합니다.
                                            
                    images 필드에는 새롭게 추가되는 사진을 보내면 됩니다.
                                            
                    request의 deleteIds 필드에는 삭제하는 사진의 ID를 담아서 보내면 됩니다.
                                            
                    삭제하는 사진이 없는 경우에도 deleteIds 빈 배열 '[]'을 반드시 보내야 합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "리뷰 수정 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.사진이 4개보다 많은 경우
                                                        
                            2.삭제 사진 필드가 없는 경우
                                                        
                            3.삭제 사진 ID에 음수가 포함된 경우
                                                        
                            4.수정하는 리뷰 내용이 없는 경우
                                                        
                            5.양수가 아닌 리뷰 ID
                                                        
                            6.삭제 사진 ID가 4개보다 많은 경우
                                                        
                            7.삭제하려는 사진이 해당 리뷰의 사진이 아닌 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리뷰가 존재하지 않을 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> update(
            @Parameter(description = "리뷰 ID", example = "1")
            @Positive(message = "리뷰 ID는 양수만 가능합니다.") Long reviewId,

            @Valid ReviewUpdateRequest reviewUpdateRequest,

            @Size(max = 4, message = "사진의 개수는 최대 4개까지 가능합니다.") List<MultipartFile> imageFiles,

            Member member
    );

    @Operation(summary = "리뷰 삭제", description = "사용자가 작성한 리뷰를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "리뷰 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.리뷰 ID 타입이 올바르지 않은 경우
                                                        
                            2.리뷰 ID가 양수가 아닌 경우
                                                        
                            3.리뷰 작성자와 요청을 보낸 사용자가 다른 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리뷰가 존재하지 않을 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "리뷰 ID", example = "1")
            @Positive(message = "리뷰 ID는 양수만 가능합니다.")
            Long reviewId,

            Member member
    );
}
