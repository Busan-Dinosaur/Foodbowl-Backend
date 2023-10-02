package org.dinosaur.foodbowl.domain.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "가게 검색 응답")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StoreSearchResponse {

    @Schema(description = "가게 ID", example = "1")
    private Long storeId;

    @Schema(description = "가게 이름", example = "김밥나라 부산대점")
    private String storeName;

    @Schema(description = "사용자로 부터 거리(단위: 미터)", example = "1250.234")
    private double distance;

    @Schema(description = "가게 리뷰 개수", example = "10")
    private long reviewCount;
}

