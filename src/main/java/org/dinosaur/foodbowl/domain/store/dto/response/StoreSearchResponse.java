package org.dinosaur.foodbowl.domain.store.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "가게 검색 응답")
public record StoreSearchResponse(
        @Schema(description = "가게 ID", example = "1")
        Long storeId,

        @Schema(description = "가게 이름", example = "김밥나라 부산대점")
        String storeName,

        @Schema(description = "사용자로 부터 거리(단위: 미터)", example = "1250.234")
        double distance,

        @Schema(description = "가게 리뷰 개수", example = "10")
        long reviewCount
) {
    @QueryProjection
    public StoreSearchResponse {
    }
}

