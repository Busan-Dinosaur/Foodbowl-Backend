package org.dinosaur.foodbowl.domain.photo.persistence;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.dinosaur.foodbowl.PersistenceTest;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class PhotoRepositoryTest extends PersistenceTest {

    @Autowired
    private PhotoRepository photoRepository;

    @Test
    void 사진을_저장한다() {
        Review review = reviewTestPersister.builder().save();
        Photo photo = Photo.builder()
                .review(review)
                .path("http://justdoeat.shop/store1/image.jpg?")
                .build();

        Photo savePhoto = photoRepository.save(photo);

        assertSoftly(
                softly -> {
                    softly.assertThat(savePhoto.getId()).isNotNull();
                    softly.assertThat(savePhoto.getReview()).isEqualTo(review);
                }
        );
    }
}
