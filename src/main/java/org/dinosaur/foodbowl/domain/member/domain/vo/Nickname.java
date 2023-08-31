package org.dinosaur.foodbowl.domain.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.member.exception.MemberExceptionType;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;

@Getter
@EqualsAndHashCode(of = {"value"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Nickname {

    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]{1,10}$");

    @NotNull
    @Column(name = "nickname", unique = true, length = 45)
    private String value;

    public Nickname(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        Matcher matcher = NICKNAME_PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new InvalidArgumentException(MemberExceptionType.INVALID_NICKNAME);
        }
    }
}
