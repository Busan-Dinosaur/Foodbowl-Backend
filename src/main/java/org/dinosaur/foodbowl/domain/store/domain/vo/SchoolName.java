package org.dinosaur.foodbowl.domain.store.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.store.exception.SchoolExceptionType;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class SchoolName {

    private static final Pattern SCHOOL_NAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9\\s]+$");

    @NotNull
    @Column(name = "name", length = 100)
    private String name;

    public SchoolName(String name) {
        validate(name);
        this.name = name;
    }

    private void validate(String name) {
        Matcher matcher = SCHOOL_NAME_PATTERN.matcher(name);
        if (!matcher.matches()) {
            throw new InvalidArgumentException(SchoolExceptionType.INVALID_SCHOOL_NAME);
        }
    }
}
