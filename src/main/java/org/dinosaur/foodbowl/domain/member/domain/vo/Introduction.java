package org.dinosaur.foodbowl.domain.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.member.exception.MemberExceptionType;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Introduction {

    private static final int MAXIMUM_LENGTH = 100;

    @Column(name = "introduction", length = 255)
    private String value;

    public Introduction(String value) {
        validate(value);
        this.value = convertWhenEmpty(value);
    }

    private void validate(String value) {
        if (isNullOrEmpty(value)) {
            return;
        }
        if (value.isBlank() || value.length() > MAXIMUM_LENGTH) {
            throw new InvalidArgumentException(MemberExceptionType.INVALID_INTRODUCTION);
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    private String convertWhenEmpty(String value) {
        if (isNullOrEmpty(value)) {
            return null;
        }
        return value;
    }
}
