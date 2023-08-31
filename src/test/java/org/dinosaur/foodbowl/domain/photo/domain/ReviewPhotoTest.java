package org.dinosaur.foodbowl.domain.photo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.domain.ReviewPhoto;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReviewPhotoTest {

    @Test
    void 리뷰_사진을_생성한다() {
        Photo photo = Photo.builder()
                .path("http://foodbowl.com/static/images/image.png")
                .build();
        Review review = Review.builder()
                .content("맛있어요")
                .build();
        ReviewPhoto reviewPhoto = ReviewPhoto.builder()
                .photo(photo)
                .review(review)
                .build();

        assertThat(reviewPhoto.getReview()).isEqualTo(review);
    }
}
