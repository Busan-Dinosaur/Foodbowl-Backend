package org.dinosaur.foodbowl.global.util;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class VerifiedCoordinateTest {

    @Test
    void X_좌표_Y_좌표로_좌표_객체를_만든다() {
        BigDecimal x = BigDecimal.valueOf(123.1234);
        BigDecimal y = BigDecimal.valueOf(37.1234);

        VerifiedCoordinate verifiedCoordinate = new VerifiedCoordinate(x, y);

        assertSoftly(softly -> {
            softly.assertThat(verifiedCoordinate.getX()).isEqualTo(x.doubleValue());
            softly.assertThat(verifiedCoordinate.getY()).isEqualTo(y.doubleValue());
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"-180.0123", "180.0123"})
    void 경도_범위를_벗어나면_예외를_던진다(String x) {
        BigDecimal y = BigDecimal.valueOf(37.1234);

        assertThatThrownBy(() -> new VerifiedCoordinate(new BigDecimal(x), y))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("경도 값의 크기가 잘못되었습니다.");

    }

    @ParameterizedTest
    @ValueSource(strings = {"-90.0123", "90.0123"})
    void 위도_범위를_벗어나면_예외를_던진다(String y) {
        BigDecimal x = BigDecimal.valueOf(123.1234);

        assertThatThrownBy(() -> new VerifiedCoordinate(x, new BigDecimal(y)))
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("위도 값의 크기가 잘못되었습니다.");
    }
}
