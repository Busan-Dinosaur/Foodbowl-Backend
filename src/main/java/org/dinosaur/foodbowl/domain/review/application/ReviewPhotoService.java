package org.dinosaur.foodbowl.domain.review.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.domain.ReviewPhoto;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewPhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewPhotoService {

    private final ReviewPhotoRepository reviewPhotoRepository;

    @Transactional(readOnly = true)
    public List<Photo> findPhotos(Review review) {
        return reviewPhotoRepository.findAllByReview(review).stream()
                .map(ReviewPhoto::getPhoto)
                .toList();
    }

    @Transactional
    public void save(Review review, List<Photo> photos) {
        for (Photo photo : photos) {
            ReviewPhoto reviewPhoto = ReviewPhoto.builder()
                    .review(review)
                    .photo(photo)
                    .build();
            reviewPhotoRepository.save(reviewPhoto);
        }
    }

    @Transactional
    public void delete(Review review) {
        reviewPhotoRepository.deleteAllByReview(review);
    }

    @Transactional
    public void deleteByReviewAndPhoto(Review review, List<Photo> photos) {
        reviewPhotoRepository.deleteByReviewAndPhotos(review, photos);
    }
}
