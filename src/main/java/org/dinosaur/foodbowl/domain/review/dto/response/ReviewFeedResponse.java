package org.dinosaur.foodbowl.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Set;
import org.dinosaur.foodbowl.domain.follow.application.dto.MemberToFollowerCountDto;
import org.dinosaur.foodbowl.domain.review.application.dto.ReviewToPhotoPathDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.global.util.PointUtils;

public record ReviewFeedResponse(
        @Schema(description = "리뷰 피드 썸네일 응답")
        String reviewFeedThumbnail,

        @Schema(description = "리뷰 작성자 응답")
        ReviewWriterResponse writer,

        @Schema(description = "리뷰 본문 응답")
        ReviewContentResponse review,

        @Schema(description = "리뷰 가게 응답")
        ReviewStoreResponse store
) {

    public static ReviewFeedResponse of(
            Review review,
            MemberToFollowerCountDto memberToFollowerCountDto,
            ReviewToPhotoPathDto reviewToPhotoPathDto,
            Set<Store> bookmarkStores,
            BigDecimal deviceX,
            BigDecimal deviceY
    ) {
        String reviewFeedThumbnail = reviewToPhotoPathDto.getPhotoPath(review.getId()).get(0);
        return new ReviewFeedResponse(
                reviewFeedThumbnail,
                ReviewWriterResponse.of(
                        review.getMember(),
                        memberToFollowerCountDto.getFollowCount(review.getMember().getId())
                ),
                ReviewContentResponse.of(
                        review,
                        reviewToPhotoPathDto.getPhotoPath(review.getId())
                ),
                ReviewStoreResponse.of(
                        review.getStore(),
                        PointUtils.calculateDistance(
                                PointUtils.generate(deviceX, deviceY),
                                review.getStore().getAddress().getCoordinate()
                        ),
                        bookmarkStores.contains(review.getStore())
                )
        );
    }
}
