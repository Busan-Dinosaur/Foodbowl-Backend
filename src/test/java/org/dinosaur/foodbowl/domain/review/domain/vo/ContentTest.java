package org.dinosaur.foodbowl.domain.review.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ContentTest {

    @Nested
    class 리뷰_내용_객체를_생성할_때 {

        @Test
        void 정상적으로_생성한다() {
            String value = "별로 맛없어요. 추천하지 않습니다.";

            Content content = new Content(value);

            assertThat(content.getValue()).isEqualTo(value);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = " ")
        void 빈_값이나_공백만_있는_내용이면_예외가_발생한다(String value) {
            assertThatThrownBy(() -> new Content(value))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("리뷰 내용은 빈 값이 될 수 없습니다.");
        }

        @Test
        void 최대_길이를_넘는_내용이면_예외가_발생한다() {
            String value = "a".repeat(256);

            assertThatThrownBy(() -> new Content(value))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("최대 글자 수를 넘는 내용은 작성할 수 없습니다.");
        }
    }
}
