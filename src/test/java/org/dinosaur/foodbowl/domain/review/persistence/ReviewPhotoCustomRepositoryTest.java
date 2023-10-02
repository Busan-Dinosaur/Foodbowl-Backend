package org.dinosaur.foodbowl.domain.review.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.domain.ReviewPhoto;
import org.dinosaur.foodbowl.domain.review.persistence.dto.ReviewPhotoPathDto;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ReviewPhotoCustomRepositoryTest extends PersistenceTest {

    @Autowired
    private ReviewPhotoRepository reviewPhotoRepository;

    @Autowired
    private ReviewPhotoCustomRepository reviewPhotoCustomRepository;

    @Test
    void 리뷰_목록에_존재하는_리뷰의_사진_경로_목록을_조회한다() {
        Review review = reviewTestPersister.builder().save();
        ReviewPhoto reviewPhotoA = reviewPhotoTestPersister.builder().review(review).save();
        ReviewPhoto reviewPhotoB = reviewPhotoTestPersister.builder().review(review).save();

        List<ReviewPhotoPathDto> result = reviewPhotoCustomRepository.findPhotoPathByReviews(List.of(review));

        List<ReviewPhotoPathDto> expected = List.of(
                new ReviewPhotoPathDto(review.getId(), reviewPhotoA.getPhoto().getPath()),
                new ReviewPhotoPathDto(review.getId(), reviewPhotoB.getPhoto().getPath())
        );
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void 리뷰_사진_엔티티를_삭제한다() {
        Review review = reviewTestPersister.builder().save();
        reviewPhotoTestPersister.builder().review(review).save();
        reviewPhotoTestPersister.builder().review(review).save();

        long deleteCount = reviewPhotoCustomRepository.deleteAllByReview(review);

        assertThat(deleteCount).isEqualTo(2);
    }

    @Test
    void 리뷰에_해당하는_사진_엔티티들을_삭제한다() {
        Review review = reviewTestPersister.builder().save();
        Photo deleteTargetPhotoA = photoTestPersister.builder().save();
        Photo deleteTargetPhotoB = photoTestPersister.builder().save();
        Photo photo = photoTestPersister.builder().save();
        ReviewPhoto deleteReviewPhotoA =
                reviewPhotoTestPersister.builder().review(review).photo(deleteTargetPhotoA).save();
        ReviewPhoto deleteReviewPhotoB =
                reviewPhotoTestPersister.builder().review(review).photo(deleteTargetPhotoB).save();
        ReviewPhoto reviewPhoto = reviewPhotoTestPersister.builder().review(review).photo(photo).save();

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
