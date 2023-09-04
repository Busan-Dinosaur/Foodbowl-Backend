package org.dinosaur.foodbowl.domain.review.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.domain.ReviewPhoto;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewPhotoRepository;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ReviewPhotoRepositoryTest extends PersistenceTest {

    @Autowired
    private ReviewPhotoRepository reviewPhotoRepository;

    @Test
    void 리뷰에_해당하는_리뷰_사진_엔티티를_조회한다() {
        Review review = reviewTestPersister.builder().save();
        Photo photo1 = photoTestPersister.builder().save();
        Photo photo2 = photoTestPersister.builder().save();
        ReviewPhoto reviewPhoto1 = ReviewPhoto.builder()
                .review(review)
                .photo(photo1)
                .build();
        ReviewPhoto reviewPhoto2 = ReviewPhoto.builder()
                .review(review)
                .photo(photo2)
                .build();
        reviewPhotoRepository.save(reviewPhoto1);
        reviewPhotoRepository.save(reviewPhoto2);

        List<ReviewPhoto> reviewPhotos = reviewPhotoRepository.findAllByReview(review);

        assertThat(reviewPhotos).contains(reviewPhoto1, reviewPhoto2);
    }

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

    @Test
    void 리뷰_사진_엔티티를_삭제한다() {
        Review review = reviewTestPersister.builder().save();
        Photo photo1 = photoTestPersister.builder().save();
        Photo photo2 = photoTestPersister.builder().save();
        ReviewPhoto reviewPhoto1 = ReviewPhoto.builder()
                .review(review)
                .photo(photo1)
                .build();
        ReviewPhoto reviewPhoto2 = ReviewPhoto.builder()
                .review(review)
                .photo(photo2)
                .build();
        reviewPhotoRepository.save(reviewPhoto1);
        reviewPhotoRepository.save(reviewPhoto2);

        long deleteCount = reviewPhotoRepository.deleteAllByReview(review);

        assertThat(deleteCount).isEqualTo(2);
    }
}
