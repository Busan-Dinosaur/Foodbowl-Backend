package org.dinosaur.foodbowl.domain.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.dinosaur.foodbowl.domain.store.domain.Store;

@Schema(description = "지도 경계 가게 응답")
public record StoreMapBoundResponse(
        @Schema(description = "가게 ID", example = "1")
        Long id,

        @Schema(description = "가게 이름", example = "홍대입구역 편의점")
        String name,

        @Schema(description = "가게 카테고리", example = "일식")
        String categoryName,

        @Schema(description = "가게 주소", example = "강원도 한섬로73 동해아파트 101동 101호")
        String addressName,

        @Schema(description = "가게 상세 정보 URL", example = "http://store.info.com")
        String url,

        @Schema(description = "가게 경도", example = "123.3636")
        double x,

        @Schema(description = "가게 위도", example = "32.3636")
        double y,

        @Schema(description = "가게 후기 개수", example = "123")
        long reviewCount,

        @Schema(description = "가게 북마크 여부", example = "false")
        boolean isBookmarked
) {

    public static StoreMapBoundResponse of(Store store, long reviewCount, boolean isBookmarked) {
        return new StoreMapBoundResponse(
                store.getId(),
                store.getStoreName(),
                store.getCategory().getName(),
                store.getAddress().getAddressName(),
                store.getStoreUrl(),
                store.getAddress().getCoordinate().getX(),
                store.getAddress().getCoordinate().getY(),
                reviewCount,
                isBookmarked
        );
    }
}
