package org.dinosaur.foodbowl.domain.store.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CategoryTypeTest {

    @ParameterizedTest
    @ValueSource(strings = {"카페", "술집", "한식", "양식", "일식", "중식", "치킨", "분식", "해산물", "샐러드", "기타"})
    void 문자와_일치하는_카테고리_타입을_반환한다(String category) {
        CategoryType categoryType = CategoryType.of(category);

        assertThat(categoryType.name()).isEqualTo(category);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"이태리", "스시", "스테이크", "학식", " "})
    void 문자와_일치하는_카테고리가_없으면_예외가_발생한다(String category) {
        assertThatThrownBy(() -> CategoryType.of(category))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("일치하는 카테고리를 찾을 수 없습니다.");
    }
}
