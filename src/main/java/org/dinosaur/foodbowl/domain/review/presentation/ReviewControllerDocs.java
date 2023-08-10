package org.dinosaur.foodbowl.domain.review.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.global.exception.ExceptionResponse;
import org.dinosaur.foodbowl.global.presentation.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "리뷰", description = "리뷰 API")
public interface ReviewControllerDocs {

    @Operation(summary = "리뷰 등록", description = "가게에 해당하는 리뷰를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "리뷰 등록 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "사진이 4개보다 많은 경우, 가게 이름 값이 없는 경우, 가게 주소 필드가 값이 없는 경우, "
                            + "가게 경도 값이 없는 경우, 가게 위도 값이 없는 경우, 가게 정보 URL이 없는 경우, "
                            + "가게 카테고리 정보가 없는 경우, 가게 리뷰 내용이 없는 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> create(
            @RequestPart(name = "request") @Valid ReviewCreateRequest reviewCreateRequest,
            @RequestPart(name = "images", required = false)
            @Size(max = 4, message = "사진의 개수는 최대 4개까지 가능합니다.") List<MultipartFile> imageFiles,
            @Auth Member member);
}
