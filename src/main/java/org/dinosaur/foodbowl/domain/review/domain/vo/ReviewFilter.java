package org.dinosaur.foodbowl.domain.review.domain.vo;

import org.dinosaur.foodbowl.domain.review.exception.ReviewExceptionType;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;

public enum ReviewFilter {

    ALL,
    FRIEND;

    public static ReviewFilter from(String reviewFilter) {
        return switch (reviewFilter) {
            case "ALL" -> ALL;
            case "FRIEND" -> FRIEND;
            default -> throw new InvalidArgumentException(ReviewExceptionType.NOT_EXIST_TYPE);
        };
    }
}
