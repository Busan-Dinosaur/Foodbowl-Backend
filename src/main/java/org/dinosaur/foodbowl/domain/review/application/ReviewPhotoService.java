package org.dinosaur.foodbowl.domain.review.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.photo.domain.ReviewPhoto;
import org.dinosaur.foodbowl.domain.photo.persistence.ReviewPhotoRepository;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewPhotoService {

    private final ReviewPhotoRepository reviewPhotoRepository;

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
}
