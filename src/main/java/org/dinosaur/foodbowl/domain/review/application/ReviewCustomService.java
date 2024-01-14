package org.dinosaur.foodbowl.domain.review.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.review.application.dto.MapCoordinateBoundDto;
import org.dinosaur.foodbowl.domain.review.application.dto.StoreToReviewCountDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.domain.vo.ReviewFilter;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewCustomRepository;
import org.dinosaur.foodbowl.domain.review.persistence.dto.StoreReviewCountDto;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewCustomService {

    private final ReviewCustomRepository reviewCustomRepository;

    @Transactional(readOnly = true)
    public StoreToReviewCountDto getReviewCountByStores(List<Store> stores) {
        List<StoreReviewCountDto> storeReviewCounts = reviewCustomRepository.findReviewCountByStores(stores);
        return StoreToReviewCountDto.from(storeReviewCounts);
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewFeeds(Long lastReviewId, int pageSize) {
        return reviewCustomRepository.findPaginationReviewsHavingPhoto(lastReviewId, pageSize);
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByMemberInMapBounds(
            Long memberId,
            Long lastReviewId,
            MapCoordinateBoundDto mapCoordinateBoundDto,
            int pageSize
    ) {
        return reviewCustomRepository.findPaginationReviewsByMemberInMapBound(
                memberId,
                lastReviewId,
                mapCoordinateBoundDto,
                pageSize
        );
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByStore(
            Long storeId,
            ReviewFilter reviewFilter,
            Long memberId,
            Long lastReviewId,
            int pageSize
    ) {
        return reviewCustomRepository.findPaginationReviewsByStore(
                storeId,
                reviewFilter,
                memberId,
                lastReviewId,
                pageSize
        );
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByBookmarkInMapBounds(
            Long memberId,
            Long lastReviewId,
            MapCoordinateBoundDto mapCoordinateBoundDto,
            int pageSize
    ) {
        return reviewCustomRepository.findPaginationReviewsByBookmarkInMapBounds(
                memberId,
                lastReviewId,
                mapCoordinateBoundDto,
                pageSize
        );
    }

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

    @Transactional(readOnly = true)
    public List<Review> getReviewsBySchoolInMapBounds(
            Long schoolId,
            Long lastReviewId,
            MapCoordinateBoundDto mapCoordinateBoundDto,
            int pageSize
    ) {
        return reviewCustomRepository.findPaginationReviewsBySchoolInMapBounds(
                schoolId,
                lastReviewId,
                mapCoordinateBoundDto,
                pageSize
        );
    }
}
