package org.dinosaur.foodbowl.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.dinosaur.foodbowl.domain.follow.application.dto.MemberToFollowerCountDto;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.application.dto.ReviewToPhotoPathDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;

@Schema(description = "가게 리뷰 단건 응답")
public record StoreReviewContentResponse(
        @Schema(description = "리뷰 작성자 응답")
        ReviewWriterResponse writer,

        @Schema(description = "리뷰 본문 응답")
        ReviewContentResponse review
) {

    public static StoreReviewContentResponse of(
            Review review,
            Member member,
            ReviewToPhotoPathDto reviewToPhotoPathDto,
            MemberToFollowerCountDto memberToFollowerCountDto
    ) {
        ReviewWriterResponse reviewWriterResponse = ReviewWriterResponse.of(
                member,
                memberToFollowerCountDto.getFollowCount(member.getId())
        );
        ReviewContentResponse reviewContentResponse = ReviewContentResponse.of(
                review,
                reviewToPhotoPathDto.getPhotoPath(review.getId())
        );
        return new StoreReviewContentResponse(reviewWriterResponse, reviewContentResponse);
    }
}
