package org.dinosaur.foodbowl.domain.review.persistence.dto;

import com.querydsl.core.annotations.QueryProjection;

public record ReviewPhotoPathDto(
        Long reviewId,
        String photoPath
) {

    @QueryProjection
    public ReviewPhotoPathDto {
    }
}
