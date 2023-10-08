package org.dinosaur.foodbowl.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.dinosaur.foodbowl.domain.follow.application.dto.MemberToFollowerCountDto;
import org.dinosaur.foodbowl.domain.review.application.dto.ReviewToPhotoPathDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.dto.request.DeviceCoordinateRequest;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.global.util.PointUtils;

public record StoreReviewResponse(
        @Schema(description = "가게 정보")
        ReviewStoreResponse reviewStoreResponse,

        @Schema(description = "가게에 대한 리뷰 내용")
        List<StoreReviewContentResponse> storeReviewContentResponses,

        @Schema(description = "리뷰 페이지 정보")
        ReviewPageInfo page
) {
    public static StoreReviewResponse of(
            Store store,
            List<Review> reviews,
            ReviewToPhotoPathDto reviewToPhotoPathDto,
            MemberToFollowerCountDto memberToFollowerCountDto,
            DeviceCoordinateRequest deviceCoordinateRequest,
            boolean isBookmark
    ) {
        ReviewStoreResponse reviewStoreResponse = getReviewStoreResponse(
                store,
                deviceCoordinateRequest,
                isBookmark
        );

        List<StoreReviewContentResponse> storeReviewContentResponses = getStoreReviewContentResponses(
                reviews,
                reviewToPhotoPathDto,
                memberToFollowerCountDto
        );

        ReviewPageInfo reviewPageInfo = ReviewPageInfo.from(reviews);
        return new StoreReviewResponse(
                reviewStoreResponse,
                storeReviewContentResponses,
                reviewPageInfo
        );
    }

    private static List<StoreReviewContentResponse> getStoreReviewContentResponses(
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

    private static ReviewStoreResponse getReviewStoreResponse(
            Store store,
            DeviceCoordinateRequest deviceCoordinateRequest,
            boolean isBookmark
    ) {
        return ReviewStoreResponse.of(
                store,
                PointUtils.calculateDistance(
                        PointUtils.generate(deviceCoordinateRequest.deviceX(), deviceCoordinateRequest.deviceY()),
                        store.getAddress().getCoordinate()
                ),
                isBookmark
        );
    }
}
