package org.dinosaur.foodbowl.domain.store.domain.vo;

import java.util.Arrays;
import lombok.Getter;
import org.dinosaur.foodbowl.domain.store.exception.CategoryExceptionType;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;

@Getter
public enum CategoryType {

    카페(1L),
    술집(2L),
    한식(3L),
    양식(4L),
    일식(5L),
    중식(6L),
    치킨(7L),
    분식(8L),
    해산물(9L),
    샐러드(10L),
    기타(11L);

    private final Long id;

    CategoryType(Long id) {
        this.id = id;
    }

    public static CategoryType of(String categoryName) {
        return Arrays.stream(values())
                .filter(categoryType -> categoryType.name().equals(categoryName))
                .findAny()
                .orElseThrow(() -> new InvalidArgumentException(CategoryExceptionType.NOT_FOUND));
    }
}
