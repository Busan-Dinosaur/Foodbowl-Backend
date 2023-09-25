package org.dinosaur.foodbowl.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dinosaur.foodbowl.domain.review.domain.Review;

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
            Map<Long, Long> memberIdFollowerCountGroup,
            Map<Long, List<String>> reviewIdPhotoPathsGroup,
            Set<Long> bookmarkStoreIds
    ) {
        return new ReviewResponse(
                ReviewWriterResponse.of(
                        review.getMember(),
                        memberIdFollowerCountGroup.getOrDefault(review.getMember().getId(), 0L)
                ),
                ReviewContentResponse.of(
                        review,
                        reviewIdPhotoPathsGroup.getOrDefault(review.getId(), Collections.emptyList())
                ),
                ReviewStoreResponse.of(
                        review.getStore(),
                        bookmarkStoreIds.contains(review.getStore().getId())
                )
        );
    }
}
