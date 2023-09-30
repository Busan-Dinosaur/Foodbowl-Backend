package org.dinosaur.foodbowl.domain.review.application;

import java.math.BigDecimal;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.application.dto.MapCoordinateBoundDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ReviewCustomServiceTest extends IntegrationTest {

    @Autowired
    private ReviewCustomService reviewCustomService;

    @Test
    void 팔로잉_하는_멤버의_리뷰_목록을_범위를_통해_조회한다() {
        Member member = memberTestPersister.memberBuilder().save();
        Member writer = memberTestPersister.memberBuilder().save();
        followTestPersister.builder().following(writer).follower(member).save();
        Store store = storeTestPersister.builder().save();
        Review review = reviewTestPersister.builder().member(writer).store(store).save();
        MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                BigDecimal.valueOf(1),
                BigDecimal.valueOf(1)
        );

        List<Review> result = reviewCustomService.getReviewsByFollowingInMapBounds(
                member.getId(),
                null,
                mapCoordinateBoundDto,
                10
        );

        Assertions.assertThat(result).containsExactly(review);
    }
}
