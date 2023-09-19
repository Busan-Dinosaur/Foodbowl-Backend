package org.dinosaur.foodbowl.domain.blame.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BlameTargetTest {

    @Nested
    class 신고_타입_생성 {

        @ParameterizedTest
        @ValueSource(strings = {"MEMBER", "REVIEW"})
        void 존재하는_신고_타입이면_신고_대상을_반환한다(String target) {
            BlameTarget blameTarget = BlameTarget.from(target);

            assertThat(blameTarget).isEqualTo(BlameTarget.valueOf(target));
        }

        @Test
        void 존재하는_신고_타입이_아니라면_예외를_던진다() {
            assertThatThrownBy(() -> BlameTarget.from("HELLO"))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("존재하지 않는 신고 타입입니다.");
        }
    }
}
