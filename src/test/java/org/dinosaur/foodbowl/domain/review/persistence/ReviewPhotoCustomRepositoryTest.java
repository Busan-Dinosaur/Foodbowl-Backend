package org.dinosaur.foodbowl.domain.review.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.domain.ReviewPhoto;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ReviewPhotoCustomRepositoryTest extends PersistenceTest {

    @Autowired
    private ReviewPhotoRepository reviewPhotoRepository;

    @Autowired
    private ReviewPhotoCustomRepositoryImpl reviewPhotoCustomRepository;

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

        long deleteCount = reviewPhotoCustomRepository.deleteAllByReview(review);

        assertThat(deleteCount).isEqualTo(2);
    }

    @Test
    void 리뷰에_해당하는_사진_엔티티들을_삭제한다() {
        Review review = reviewTestPersister.builder().save();
        Photo deleteTargetPhotoA = photoTestPersister.builder().save();
        Photo deleteTargetPhotoB = photoTestPersister.builder().save();
        Photo photo = photoTestPersister.builder().save();
        ReviewPhoto deleteReviewPhotoA = ReviewPhoto.builder()
                .review(review)
                .photo(deleteTargetPhotoA)
                .build();
        ReviewPhoto deleteReviewPhotoB = ReviewPhoto.builder()
                .review(review)
                .photo(deleteTargetPhotoB)
                .build();
        ReviewPhoto reviewPhoto = ReviewPhoto.builder()
                .review(review)
                .photo(photo)
                .build();
        reviewPhotoRepository.save(deleteReviewPhotoA);
        reviewPhotoRepository.save(deleteReviewPhotoB);
        reviewPhotoRepository.save(reviewPhoto);

        List<Photo> deleteTargetPhotos = List.of(deleteTargetPhotoA, deleteTargetPhotoB);
        long deleteCount = reviewPhotoCustomRepository.deleteByReviewAndPhotos(review, deleteTargetPhotos);

        assertSoftly(softly -> {
            softly.assertThat(deleteCount).isEqualTo(deleteTargetPhotos.size());
            softly.assertThat(reviewPhotoRepository.findAllByReview(review)).containsExactly(reviewPhoto);
            softly.assertThat(reviewPhotoRepository.findAllByReview(review))
                    .doesNotContain(deleteReviewPhotoA, deleteReviewPhotoB);
        });
    }
}
