package org.dinosaur.foodbowl.domain.review.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.review.application.dto.MapCoordinateBoundDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewCustomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewCustomService {

    private final ReviewCustomRepository reviewCustomRepository;

    @Transactional(readOnly = true)
    public List<Review> getReviewsByFollowingInMapBounds(
            Long followerId,
            Long lastReviewId,
            MapCoordinateBoundDto mapCoordinateBoundDto,
            int pageSize
    ) {
        return reviewCustomRepository.findPaginationReviewsByFollowingInMapBounds(
                followerId,
                lastReviewId,
                mapCoordinateBoundDto,
                pageSize
        );
    }
}
