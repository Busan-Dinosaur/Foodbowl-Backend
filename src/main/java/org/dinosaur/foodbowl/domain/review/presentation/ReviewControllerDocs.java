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
import org.dinosaur.foodbowl.domain.review.application.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.domain.review.application.dto.request.ReviewUpdateRequest;
import org.dinosaur.foodbowl.global.exception.response.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "리뷰", description = "리뷰 API")
public interface ReviewControllerDocs {

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
                    description = "존재하지 않는 리뷰",
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
                            1.올바르지 않은 리뷰 ID 타입
                                                        
                            2.양수가 아닌 리뷰 ID
                                                        
                            3.리뷰 작성자와 요청을 보낸 사용자가 다른 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 리뷰",
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
