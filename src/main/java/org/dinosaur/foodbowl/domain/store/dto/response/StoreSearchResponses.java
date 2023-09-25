package org.dinosaur.foodbowl.domain.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "가게 검색 결과 응답")
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StoreSearchResponses {

    @Schema(description = "가게 검색 결과 목록")
    private final List<StoreSearchResponse> searchResponses;

    public static StoreSearchResponses from(List<StoreSearchResponse> searchResponses) {
        return new StoreSearchResponses(searchResponses);
    }
}
