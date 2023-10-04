package org.dinosaur.foodbowl.domain.review.persistence.dto;

import com.querydsl.core.annotations.QueryProjection;

public record StoreReviewCountDto(
        Long storeId,
        long reviewCount
) {

    @QueryProjection
    public StoreReviewCountDto {
    }
}
