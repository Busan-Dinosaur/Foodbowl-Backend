package org.dinosaur.foodbowl.domain.review.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.review.application.dto.ReviewToPhotoPathDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewPhotoCustomRepository;
import org.dinosaur.foodbowl.domain.review.persistence.dto.ReviewPhotoPathDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewPhotoCustomService {

    private final ReviewPhotoCustomRepository reviewPhotoCustomRepository;

    @Transactional(readOnly = true)
    public ReviewToPhotoPathDto getPhotoPathByReviews(List<Review> reviews) {
        List<ReviewPhotoPathDto> reviewPhotoPaths = reviewPhotoCustomRepository.findPhotoPathByReviews(reviews);
        return ReviewToPhotoPathDto.from(reviewPhotoPaths);
    }
}
