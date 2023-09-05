package org.dinosaur.foodbowl.domain.member.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class IntroductionTest {

    @Nested
    class 한_줄_소개_생성_시 {

        @ParameterizedTest
        @NullAndEmptySource
        void 빈값이거나_null이라면_null로_변환한다(String value) {
            Introduction introduction = new Introduction(value);

            assertThat(introduction.getValue()).isNull();
        }

        @Test
        void 빈값이거나_null이_아니라면_값을_유지한다() {
            Introduction introduction = new Introduction("hello");

            assertThat(introduction.getValue()).isEqualTo("hello");
        }
    }
}
