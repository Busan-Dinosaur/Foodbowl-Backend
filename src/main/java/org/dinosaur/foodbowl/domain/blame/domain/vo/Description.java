package org.dinosaur.foodbowl.domain.blame.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.blame.exception.BlameExceptionType;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Description {

    private static final int MAXIMUM_LENGTH = 255;

    @NotNull
    @Column(name = "description", updatable = false, length = MAXIMUM_LENGTH)
    private String value;

    public Description(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidArgumentException(BlameExceptionType.DESCRIPTION_EMPTY);
        }
        if (value.length() > MAXIMUM_LENGTH) {
            throw new InvalidArgumentException(BlameExceptionType.INVALID_DESCRIPTION_LENGTH);
        }
    }
}
