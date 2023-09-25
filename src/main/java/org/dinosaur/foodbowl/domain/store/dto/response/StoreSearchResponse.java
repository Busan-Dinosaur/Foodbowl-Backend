package org.dinosaur.foodbowl.domain.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "가게 검색 응답")
@Getter
public class StoreSearchResponse implements StoreSearchQueryResponse {

    private Long storeId;
    private String storeName;
    private double distance;
}
