package org.dinosaur.foodbowl.domain.photo.persistence;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.photo.domain.ReviewPhoto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ReviewPhotoRepositoryTest extends PersistenceTest {

    @Autowired
    private ReviewPhotoRepository reviewPhotoRepository;

    @Test
    void 리뷰_사진_엔티티를_저장한다() {
        Review review = reviewTestPersister.builder().save();
        Photo photo = photoTestPersister.builder().save();
        ReviewPhoto reviewPhoto = ReviewPhoto.builder()
                .review(review)
                .photo(photo)
                .build();

        ReviewPhoto savedReviewPhoto = reviewPhotoRepository.save(reviewPhoto);

        assertSoftly(
                softly -> {
                    softly.assertThat(savedReviewPhoto.getId()).isNotNull();
                    softly.assertThat(savedReviewPhoto.getReview()).isEqualTo(review);
                    softly.assertThat(savedReviewPhoto.getPhoto()).isEqualTo(photo);
                }
        );
    }
}
