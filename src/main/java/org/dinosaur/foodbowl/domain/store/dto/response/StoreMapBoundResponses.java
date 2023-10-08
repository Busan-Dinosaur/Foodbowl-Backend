package org.dinosaur.foodbowl.domain.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Set;
import org.dinosaur.foodbowl.domain.review.application.dto.StoreToReviewCountDto;
import org.dinosaur.foodbowl.domain.store.domain.Store;

@Schema(description = "지도 경계 가게 조회 응답")
public record StoreMapBoundResponses(
        @Schema(description = "지도 경계 가게 응답 목록")
        List<StoreMapBoundResponse> stores
) {

    public static StoreMapBoundResponses of(
            List<Store> stores,
            StoreToReviewCountDto storeToReviewCountDto,
            Set<Store> bookmarkStores
    ) {
        List<StoreMapBoundResponse> storeMapBounds =
                convertToStoreMapBoundResponses(stores, storeToReviewCountDto, bookmarkStores);
        return new StoreMapBoundResponses(storeMapBounds);
    }

    private static List<StoreMapBoundResponse> convertToStoreMapBoundResponses(
            List<Store> stores,
            StoreToReviewCountDto storeToReviewCountDto,
            Set<Store> bookmarkStores
    ) {
        return stores.stream()
                .map(
                        store -> StoreMapBoundResponse.of(
                                store,
                                storeToReviewCountDto.getReviewCount(store.getId()),
                                bookmarkStores.contains(store)
                        )
                )
                .toList();
    }
}
