package org.dinosaur.foodbowl.domain.review.application.dto;

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dinosaur.foodbowl.domain.review.persistence.dto.StoreReviewCountDto;

public record StoreToReviewCountDto(
        Map<Long, Long> storeToReviewCount
) {

    public static StoreToReviewCountDto from(List<StoreReviewCountDto> storeReviewCounts) {
        Map<Long, Long> storeToReviewCount = storeReviewCounts.stream()
                .collect(
                        toMap(
                                StoreReviewCountDto::storeId,
                                StoreReviewCountDto::reviewCount,
                                (exist, replace) -> replace,
                                HashMap::new
                        )
                );
        return new StoreToReviewCountDto(storeToReviewCount);
    }

    public long getReviewCount(Long storeId) {
        return storeToReviewCount.getOrDefault(storeId, 0L);
    }
}
