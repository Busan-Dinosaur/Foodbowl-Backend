package org.dinosaur.foodbowl.domain.review.persistence;

import java.util.List;
import org.dinosaur.foodbowl.domain.review.application.dto.CoordinateBoundDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;

public interface ReviewCustomRepository {

    List<Review> getPaginationReviewsByFollowing(
            Long memberId,
            Long lastReviewId,
            CoordinateBoundDto coordinateBoundDto,
            int pageSize
    );
}
