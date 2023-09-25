package org.dinosaur.foodbowl.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import org.dinosaur.foodbowl.domain.store.domain.Store;

@Schema(description = "리뷰 가게 응답")
public record ReviewStoreResponse(
        @Schema(description = "가게 ID", example = "1")
        Long id,

        @Schema(description = "가게 카테고리", example = "카페")
        String categoryName,

        @Schema(description = "가게명", example = "맛있는 가게")
        String name,

        @Schema(description = "가게 주소", example = "부산광역시 금정구 40-4")
        String addressName,

        @Schema(description = "가게 경도", example = "123.3636")
        BigDecimal x,

        @Schema(description = "가게 위도", example = "32.3636")
        BigDecimal y,

        @Schema(description = "가게 북마크 여부", example = "false")
        boolean isBookmarked
) {

    public static ReviewStoreResponse of(Store store, boolean isBookmarked) {
        return new ReviewStoreResponse(
                store.getId(),
                store.getCategory().getName(),
                store.getStoreName(),
                store.getAddress().getAddressName(),
                BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                isBookmarked
        );
    }
}
