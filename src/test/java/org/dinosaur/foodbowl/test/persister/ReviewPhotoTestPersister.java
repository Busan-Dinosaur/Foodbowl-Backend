package org.dinosaur.foodbowl.test.persister;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.domain.ReviewPhoto;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewPhotoRepository;

@RequiredArgsConstructor
@Persister
public class ReviewPhotoTestPersister {

    private final ReviewPhotoRepository reviewPhotoRepository;
    private final ReviewTestPersister reviewTestPersister;
    private final PhotoTestPersister photoTestPersister;

    public ReviewPhotoBuilder builder() {
        return new ReviewPhotoBuilder();
    }

    public final class ReviewPhotoBuilder {

        private Review review;
        private Photo photo;

        public ReviewPhotoBuilder review(Review review) {
            this.review = review;
            return this;
        }

        public ReviewPhotoBuilder photo(Photo photo) {
            this.photo = photo;
            return this;
        }

        public ReviewPhoto save() {
            ReviewPhoto reviewPhoto = ReviewPhoto.builder()
                    .review(review == null ? reviewTestPersister.builder().save() : review)
                    .photo(photo == null ? photoTestPersister.builder().save() : photo)
                    .build();
            return reviewPhotoRepository.save(reviewPhoto);
        }
    }
}
