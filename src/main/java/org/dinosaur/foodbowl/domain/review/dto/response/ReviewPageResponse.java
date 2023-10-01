package org.dinosaur.foodbowl.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.dinosaur.foodbowl.domain.follow.application.dto.MemberToFollowerCountDto;
import org.dinosaur.foodbowl.domain.review.application.dto.ReviewToPhotoPathDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.store.domain.Store;

@Schema(description = "내 팔로잉의 리뷰 조회 응답")
public record ReviewPageResponse(
        @Schema(description = "리뷰 응답 목록")
        List<ReviewResponse> reviews,

        @Schema(description = "리뷰 페이지 정보")
        ReviewPageInfo page
) {

    public static ReviewPageResponse of(
            List<Review> reviews,
            MemberToFollowerCountDto memberToFollowerCountDto,
            ReviewToPhotoPathDto reviewToPhotoPathDto,
            Set<Store> bookmarkStores,
            BigDecimal deviceX,
            BigDecimal deviceY
    ) {
        return new ReviewPageResponse(
                convertToReviewResponses(
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

    private static List<ReviewResponse> convertToReviewResponses(
            List<Review> reviews,
            MemberToFollowerCountDto memberToFollowerCountDto,
            ReviewToPhotoPathDto reviewToPhotoPathDto,
            Set<Store> bookmarkStores,
            BigDecimal deviceX,
            BigDecimal deviceY
    ) {
        return reviews.stream()
                .map(review ->
                        ReviewResponse.of(
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
