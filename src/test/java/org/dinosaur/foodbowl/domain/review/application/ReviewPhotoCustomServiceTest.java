package org.dinosaur.foodbowl.domain.review.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.dinosaur.foodbowl.domain.review.application.dto.ReviewToPhotoPathDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.domain.ReviewPhoto;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ReviewPhotoCustomServiceTest extends IntegrationTest {

    @Autowired
    private ReviewPhotoCustomService reviewPhotoCustomService;

    @Test
    void 리뷰_목록의_이미지_경로를_집계한다() {
        Review reviewA = reviewTestPersister.builder().save();
        Review reviewB = reviewTestPersister.builder().save();
        ReviewPhoto reviewPhotoA = reviewPhotoTestPersister.builder().review(reviewA).save();
        ReviewPhoto reviewPhotoB = reviewPhotoTestPersister.builder().review(reviewA).save();
        ReviewPhoto reviewPhotoC = reviewPhotoTestPersister.builder().review(reviewB).save();
        ReviewPhoto reviewPhotoD = reviewPhotoTestPersister.builder().review(reviewB).save();

        ReviewToPhotoPathDto result = reviewPhotoCustomService.getPhotoPathByReviews(List.of(reviewA, reviewB));

        assertSoftly(softly -> {
            softly.assertThat(result.getPhotoPath(reviewA.getId()))
                    .containsExactly(reviewPhotoA.getPhoto().getPath(), reviewPhotoB.getPhoto().getPath());
            softly.assertThat(result.getPhotoPath(reviewB.getId()))
                    .containsExactly(reviewPhotoC.getPhoto().getPath(), reviewPhotoD.getPhoto().getPath());
        });
    }
}
