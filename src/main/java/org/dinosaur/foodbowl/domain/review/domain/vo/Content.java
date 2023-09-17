package org.dinosaur.foodbowl.domain.review.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.review.exception.ReviewExceptionType;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content {

    private static final int MAX_LENGTH = 100;

    @NotNull
    @Column(name = "content", length = 255)
    private String value;

    public Content(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidArgumentException(ReviewExceptionType.EMPTY_CONTENT);
        }

        if (value.length() > MAX_LENGTH) {
            throw new InvalidArgumentException(ReviewExceptionType.INVALID_CONTENT);
        }
    }

    public String getValue() {
        return value;
    }
}
