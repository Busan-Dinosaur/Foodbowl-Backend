package org.dinosaur.foodbowl.domain.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "가게 검색 결과 응답")
public record StoreSearchResponses(
        @Schema(description = "가게 검색 응답 목록")
        List<StoreSearchResponse> searchResponses
) {

    public static StoreSearchResponses from(List<StoreSearchResponse> searchResponses) {
        return new StoreSearchResponses(searchResponses);
    }
}
