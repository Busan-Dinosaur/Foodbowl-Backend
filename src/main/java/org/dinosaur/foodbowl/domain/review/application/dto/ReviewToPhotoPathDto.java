package org.dinosaur.foodbowl.domain.review.application.dto;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.dinosaur.foodbowl.domain.review.persistence.dto.ReviewPhotoPathDto;

public record ReviewToPhotoPathDto(
        Map<Long, List<String>> reviewToPhotoPath
) {

    public static ReviewToPhotoPathDto from(List<ReviewPhotoPathDto> reviewPhotoPaths) {
        Map<Long, List<String>> reviewToPhotoPath = reviewPhotoPaths.stream()
                .collect(
                        groupingBy(
                                ReviewPhotoPathDto::reviewId,
                                mapping(ReviewPhotoPathDto::photoPath, toList())
                        )
                );
        return new ReviewToPhotoPathDto(reviewToPhotoPath);
    }

    public List<String> getPhotoPath(Long reviewId) {
        return reviewToPhotoPath.getOrDefault(reviewId, new ArrayList<>());
    }
}
