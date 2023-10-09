package org.dinosaur.foodbowl.domain.review.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ReviewFilterTest {

    @ParameterizedTest
    @ValueSource(strings = {"ALL", "FRIEND"})
    void 필터링_조건과_일치하는_객체를_생성한다(String filter) {
        ReviewFilter reviewFilter = ReviewFilter.from(filter);

        assertThat(reviewFilter.name()).isEqualTo(filter);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "", "test", "!@$%"})
    void 필터링_조건과_일치하지_않으면_예외가_발생한다(String filter) {
        assertThatThrownBy(() -> ReviewFilter.from(filter))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("일치하는 리뷰 필터링 조건이 없습니다.");
    }
}
