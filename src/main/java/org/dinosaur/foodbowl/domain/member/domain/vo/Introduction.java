package org.dinosaur.foodbowl.domain.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.member.exception.MemberExceptionType;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Introduction {

    private static final Pattern INTRODUCTION_PATTERN = Pattern.compile("^[가-힣a-zA-Z]{1,100}$");

    @Column(name = "introduction", length = 255)
    private String value;

    public Introduction(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        Matcher matcher = INTRODUCTION_PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new InvalidArgumentException(MemberExceptionType.INVALID_INTRODUCTION);
        }
    }
}
