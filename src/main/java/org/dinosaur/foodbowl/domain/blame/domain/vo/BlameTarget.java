package org.dinosaur.foodbowl.domain.blame.domain.vo;

import org.dinosaur.foodbowl.domain.blame.exception.BlameExceptionType;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;

public enum BlameTarget {

    MEMBER,
    REVIEW;

    public static BlameTarget from(String blameTarget) {
        return switch (blameTarget) {
            case "MEMBER" -> MEMBER;
            case "REVIEW" -> REVIEW;
            default -> throw new InvalidArgumentException(BlameExceptionType.NOT_EXIST_TYPE);
        };
    }
}
