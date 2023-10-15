package org.dinosaur.foodbowl.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.dinosaur.foodbowl.domain.review.domain.Review;

@Schema(description = "리뷰 페이지 정보")
public record ReviewPageInfo(
        @Schema(description = "페이지 첫 리뷰 ID", example = "10")
        Long firstId,

        @Schema(description = "페이지 마지막 리뷰 ID", example = "1")
        Long lastId,

        @Schema(description = "페이지 크기", example = "10")
        int size
) {

    public static ReviewPageInfo from(List<Review> reviews) {
        int size = reviews.size();
        return new ReviewPageInfo(
                size == 0 ? null : reviews.get(0).getId(),
                size == 0 ? null : reviews.get(size - 1).getId(),
                size
        );
    }
}
