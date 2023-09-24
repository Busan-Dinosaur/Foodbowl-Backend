package org.dinosaur.foodbowl.domain.review.dto.request;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "리뷰 작성 요청")
public record ReviewCreateRequest(
        @Schema(description = "장소 ID(카카오 반환 값)", example = "23149124", requiredMode = REQUIRED)
        @NotBlank(message = "장소 ID는 반드시 포함되어야 합니다.")
        String locationId,

        @Schema(description = "가게 이름", example = "맥도날드 강남점", requiredMode = REQUIRED)
        @NotBlank(message = "가게 이름은 반드시 포함되어야 합니다.")
        String storeName,

        @Schema(description = "가게 주소", example = "서울시 강남구 테헤란로 123", requiredMode = REQUIRED)
        @NotBlank(message = "가게 주소는 반드시 포함되어야 합니다.")
        String storeAddress,

        @Schema(description = "가게 경도", example = "123.31245", requiredMode = REQUIRED)
        @NotNull(message = "가게 경도는 반드시 포함되어야 합니다.")
        BigDecimal x,

        @Schema(description = "가게 위도", example = "37.1426", requiredMode = REQUIRED)
        @NotNull(message = "가게 위도는 반드시 포함되어야 합니다.")
        BigDecimal y,

        @Schema(description = "카카오 가게 정보 URL", example = "https://image.kakao.com", requiredMode = REQUIRED)
        @NotBlank(message = "카카오 서버의 가게 정보 url 반드시 포함되어야 합니다.")
        String storeUrl,

        @Schema(description = "가게 전화번호", example = "02-1234-5678")
        String phone,

        @Schema(description = "가게 카테고리", example = "한식", requiredMode = REQUIRED)
        @NotBlank(message = "가게 카테고리는 반드시 포함되어야 합니다.")
        String category,

        @Schema(description = "가게 리뷰 내용", example = "맛있습니다. 빨리 나와요.", requiredMode = REQUIRED)
        @NotBlank(message = "가게 리뷰는 반드시 포함되어야 합니다.")
        String reviewContent,

        @Schema(description = "학교 이름", example = "부산대학교")
        String schoolName,

        @Schema(description = "학교 주소", example = "부산광역시 금정구 부산대학로63번길 2")
        String schoolAddress,

        @Schema(description = "학교 경도", example = "126.12557")
        BigDecimal schoolX,

        @Schema(description = "학교 위도", example = "37.94897")
        BigDecimal schoolY
) {
}
