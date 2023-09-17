package org.dinosaur.foodbowl.domain.review.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewPhotoRepository;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ReviewPhotoServiceTest extends IntegrationTest {

    @Autowired
    private ReviewPhotoService reviewPhotoService;

    @Autowired
    private ReviewPhotoRepository reviewPhotoRepository;

    @Test
    void 리뷰_사진_매핑_정보를_저장한다() {
        Review review = reviewTestPersister.builder().save();
        Photo photoA = photoTestPersister.builder().save();
        Photo photoB = photoTestPersister.builder().save();

        reviewPhotoService.save(review, List.of(photoA, photoB));

        assertThat(reviewPhotoRepository.findAllByReview(review)).hasSize(2);
    }

    @Test
    void 리뷰_사진_매핑_정보를_삭제한다() {
        Review review = reviewTestPersister.builder().save();
        Photo photoA = photoTestPersister.builder().save();
        Photo photoB = photoTestPersister.builder().save();
        reviewPhotoService.save(review, List.of(photoA, photoB));

        reviewPhotoService.delete(review);

        assertThat(reviewPhotoRepository.findAllByReview(review)).isEmpty();
    }

    @Test
    void 리뷰_사진_매핑_정보들을_삭제한다() {
        Review review = reviewTestPersister.builder().save();
        Photo photoA = photoTestPersister.builder().save();
        Photo photoB = photoTestPersister.builder().save();
        Photo photoC = photoTestPersister.builder().save();
        reviewPhotoService.save(review, List.of(photoA, photoB, photoC));

        List<Photo> deletePhotos = List.of(photoA, photoB);
        reviewPhotoService.deleteByReviewAndPhoto(review, deletePhotos);

        assertThat(reviewPhotoRepository.findAllByReview(review)).hasSize(1);
    }
}
