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
class ReviewPhotoCustomRepositoryImplTest extends PersistenceTest {

    @Autowired
    private ReviewPhotoRepository reviewPhotoRepository;

    @Autowired
    private ReviewPhotoCustomRepositoryImpl reviewPhotoCustomRepositoryImpl;

    @Test
    void 리뷰_사진_엔티티를_삭제한다() {
        Review review = reviewTestPersister.builder().save();
        Photo photoA = photoTestPersister.builder().save();
        Photo photoB = photoTestPersister.builder().save();
        ReviewPhoto reviewPhotoA = ReviewPhoto.builder()
                .review(review)
                .photo(photoA)
                .build();
        ReviewPhoto reviewPhotoB = ReviewPhoto.builder()
                .review(review)
                .photo(photoB)
                .build();
        reviewPhotoRepository.save(reviewPhotoA);
        reviewPhotoRepository.save(reviewPhotoB);

        long deleteCount = reviewPhotoCustomRepositoryImpl.deleteAllByReview(review);

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
        long deleteCount = reviewPhotoCustomRepositoryImpl.deleteByReviewAndPhotos(review, deleteTargetPhotos);

        assertSoftly(softly -> {
            softly.assertThat(deleteCount).isEqualTo(deleteTargetPhotos.size());
            softly.assertThat(reviewPhotoRepository.findAllByReview(review)).containsExactly(reviewPhoto);
            softly.assertThat(reviewPhotoRepository.findAllByReview(review))
                    .doesNotContain(deleteReviewPhotoA, deleteReviewPhotoB);
        });
    }
}
