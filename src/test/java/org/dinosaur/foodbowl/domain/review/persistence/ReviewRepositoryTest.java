package org.dinosaur.foodbowl.domain.review.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.PersistenceTest;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReviewRepositoryTest extends PersistenceTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void 리뷰를_저장한다() {
        Member member = memberTestPersister.memberBuilder().save();
        Store store = storeTestPersister.builder().save();
        Review review = Review.builder()
                .member(member)
                .store(store)
                .content("정말 맛있습니다.")
                .build();

        Review saveReview = reviewRepository.save(review);

        assertThat(saveReview.getId()).isNotNull();
    }
}
