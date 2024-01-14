package org.dinosaur.foodbowl.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.dinosaur.foodbowl.domain.follow.application.dto.MemberToFollowerCountDto;
import org.dinosaur.foodbowl.domain.review.application.dto.ReviewToPhotoPathDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.store.domain.Store;

@Schema(description = "리뷰 피드 조회 페이지 응답")
public record ReviewFeedPageResponse(
        @Schema(description = "리뷰 피드 페이지 응답")
        List<ReviewFeedResponse> reviewFeedResponses,

        @Schema(description = "리뷰 조회 페이지 정보")
        ReviewPageInfo reviewPageInfo
) {

    public static ReviewFeedPageResponse of(
            List<Review> reviews,
            MemberToFollowerCountDto memberToFollowerCountDto,
            ReviewToPhotoPathDto reviewToPhotoPathDto,
            Set<Store> bookmarkStores,
            BigDecimal deviceX,
            BigDecimal deviceY
    ) {
        return new ReviewFeedPageResponse(
                convertToReviewFeedResponses(
                        reviews,
                        memberToFollowerCountDto,
                        reviewToPhotoPathDto,
                        bookmarkStores,
                        deviceX,
                        deviceY
                ),
                ReviewPageInfo.from(reviews)
        );
    }

    private static List<ReviewFeedResponse> convertToReviewFeedResponses(
            List<Review> reviews,
            MemberToFollowerCountDto memberToFollowerCountDto,
            ReviewToPhotoPathDto reviewToPhotoPathDto,
            Set<Store> bookmarkStores,
            BigDecimal deviceX,
            BigDecimal deviceY
    ) {
        return reviews.stream()
                .map(review ->
                        ReviewFeedResponse.of(
                                review,
                                memberToFollowerCountDto,
                                reviewToPhotoPathDto,
                                bookmarkStores,
                                deviceX,
                                deviceY
                        )
                )
                .toList();
    }
}
