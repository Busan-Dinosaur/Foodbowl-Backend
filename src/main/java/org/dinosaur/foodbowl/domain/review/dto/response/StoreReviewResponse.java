package org.dinosaur.foodbowl.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.dinosaur.foodbowl.domain.follow.application.dto.MemberToFollowerCountDto;
import org.dinosaur.foodbowl.domain.review.application.dto.ReviewToPhotoPathDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;

@Schema(description = "가게 리뷰 조회 응답")
public record StoreReviewResponse(
        @Schema(description = "가게에 대한 리뷰 내용")
        List<StoreReviewContentResponse> storeReviewContentResponses,

        @Schema(description = "리뷰 페이지 정보")
        ReviewPageInfo page
) {

    public static StoreReviewResponse of(
            List<Review> reviews,
            ReviewToPhotoPathDto reviewToPhotoPathDto,
            MemberToFollowerCountDto memberToFollowerCountDto
    ) {
        List<StoreReviewContentResponse> storeReviewContentResponses = convertStoreReviewContentResponses(
                reviews,
                reviewToPhotoPathDto,
                memberToFollowerCountDto
        );

        ReviewPageInfo reviewPageInfo = ReviewPageInfo.from(reviews);
        return new StoreReviewResponse(
                storeReviewContentResponses,
                reviewPageInfo
        );
    }

    private static List<StoreReviewContentResponse> convertStoreReviewContentResponses(
            List<Review> reviews,
            ReviewToPhotoPathDto reviewToPhotoPathDto,
            MemberToFollowerCountDto memberToFollowerCountDto
    ) {
        return reviews.stream()
                .map(review -> StoreReviewContentResponse.of(
                        review,
                        review.getMember(),
                        reviewToPhotoPathDto,
                        memberToFollowerCountDto
                ))
                .toList();
    }
}
