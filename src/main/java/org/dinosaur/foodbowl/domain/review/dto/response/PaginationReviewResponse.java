package org.dinosaur.foodbowl.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dinosaur.foodbowl.domain.review.domain.Review;

@Schema(description = "리뷰 목록 페이지 조회 응답")
public record PaginationReviewResponse(
        @Schema(description = "리뷰 응답 목록")
        List<ReviewResponse> reviews,

        @Schema(description = "리뷰 페이지 정보")
        ReviewPageInfo page
) {

    public static PaginationReviewResponse of(
            List<Review> reviews,
            Map<Long, Long> memberIdFollowerCountGroup,
            Map<Long, List<String>> reviewIdPhotoPathsGroup,
            Set<Long> bookmarkStoreIds
    ) {
        return new PaginationReviewResponse(
                convertToResponses(reviews, memberIdFollowerCountGroup, reviewIdPhotoPathsGroup, bookmarkStoreIds),
                ReviewPageInfo.from(reviews)
        );
    }

    private static List<ReviewResponse> convertToResponses(
            List<Review> reviews,
            Map<Long, Long> memberIdFollowerCountGroup,
            Map<Long, List<String>> reviewIdPhotoPathsGroup,
            Set<Long> bookmarkStoreIds
    ) {
        return reviews.stream()
                .map(review ->
                        ReviewResponse.of(
                                review,
                                memberIdFollowerCountGroup,
                                reviewIdPhotoPathsGroup,
                                bookmarkStoreIds
                        )
                )
                .toList();
    }
}
