package org.dinosaur.foodbowl.domain.review.application.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.dinosaur.foodbowl.domain.review.persistence.dto.ReviewPhotoPathDto;

public record ReviewToPhotoPathDto(
        Map<Long, List<String>> reviewToPhotoPath
) {

    public static ReviewToPhotoPathDto from(List<ReviewPhotoPathDto> reviewPhotoPaths) {
        Map<Long, List<String>> reviewToPhotoPath = reviewPhotoPaths.stream()
                .collect(
                        Collectors.groupingBy(
                                ReviewPhotoPathDto::reviewId,
                                Collectors.mapping(ReviewPhotoPathDto::photoPath, Collectors.toList())
                        )
                );
        return new ReviewToPhotoPathDto(reviewToPhotoPath);
    }

    public List<String> getPhotoPath(Long reviewId) {
        return reviewToPhotoPath.getOrDefault(reviewId, new ArrayList<>());
    }
}
