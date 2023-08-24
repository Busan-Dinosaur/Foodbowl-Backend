package org.dinosaur.foodbowl.domain.review.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ReviewPhotoServiceTest extends IntegrationTest {

    @Autowired
    private ReviewPhotoService reviewPhotoService;

    @Test
    void 리뷰_사진_매핑_정보를_저장한다() {
        Review review = reviewTestPersister.builder().save();
        Photo photo1 = photoTestPersister.builder().save();
        Photo photo2 = photoTestPersister.builder().save();

        assertDoesNotThrow(() -> reviewPhotoService.save(review, List.of(photo1, photo2)));
    }
}
