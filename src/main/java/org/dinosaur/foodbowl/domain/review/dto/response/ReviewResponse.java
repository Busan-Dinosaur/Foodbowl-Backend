package org.dinosaur.foodbowl.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Set;
import org.dinosaur.foodbowl.domain.follow.application.dto.MemberToFollowerCountDto;
import org.dinosaur.foodbowl.domain.review.application.dto.ReviewToPhotoPathDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.global.util.PointUtils;

@Schema(description = "리뷰 응답")
public record ReviewResponse(
        @Schema(description = "리뷰 작성자 응답")
        ReviewWriterResponse writer,

        @Schema(description = "리뷰 본문 응답")
        ReviewContentResponse review,

        @Schema(description = "리뷰 가게 응답")
        ReviewStoreResponse store
) {

    public static ReviewResponse of(
            Review review,
            MemberToFollowerCountDto memberToFollowerCountDto,
            ReviewToPhotoPathDto reviewToPhotoPathDto,
            Set<Store> bookmarkStores,
            BigDecimal deviceX,
            BigDecimal deviceY
    ) {
        return new ReviewResponse(
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
