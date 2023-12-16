package org.dinosaur.foodbowl.domain.review.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.*;

import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ReviewRepositoryTest extends PersistenceTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void 리뷰를_조회한다() {
        Member member = memberTestPersister.builder().save();
        Store store = storeTestPersister.builder().save();
        Review review = Review.builder()
                .member(member)
                .store(store)
                .content("정말 맛있습니다.")
                .build();
        Review saveReview = reviewRepository.save(review);

        assertThat(reviewRepository.findById(saveReview.getId())).isPresent();
    }

    @Test
    void 리뷰를_조회할_때_가게와_작성를_함께_조회한다() {
        Member member = memberTestPersister.builder().save();
        Store store = storeTestPersister.builder().save();
        Review review = Review.builder()
                .member(member)
                .store(store)
                .content("정말 좋습니다.")
                .build();
        Review saveReview = reviewRepository.save(review);

        Review foundReview = reviewRepository.findWithStoreAndMemberById(saveReview.getId()).get();

        assertSoftly(softly -> {
            softly.assertThat(foundReview.getId()).isPositive();
            softly.assertThat(foundReview.getStore()).isEqualTo(store);
            softly.assertThat(foundReview.getMember()).isEqualTo(member);
        });
    }

    @Test
    void 리뷰를_저장한다() {
        Member member = memberTestPersister.builder().save();
        Store store = storeTestPersister.builder().save();
        Review review = Review.builder()
                .member(member)
                .store(store)
                .content("정말 맛있습니다.")
                .build();

        Review saveReview = reviewRepository.save(review);

        assertThat(saveReview.getId()).isNotNull();
    }

    @Test
    void 리뷰를_삭제한다() {
        Member member = memberTestPersister.builder().save();
        Store store = storeTestPersister.builder().save();
        Review review = Review.builder()
                .member(member)
                .store(store)
                .content("정말 맛있습니다.")
                .build();
        Review saveReview = reviewRepository.save(review);

        reviewRepository.delete(saveReview);

        assertThat(reviewRepository.findById(saveReview.getId())).isEmpty();
    }
}
