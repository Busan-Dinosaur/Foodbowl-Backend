package org.dinosaur.foodbowl.domain.store.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class CoordinateTest {

    @Test
    void X_좌표_Y_좌표로_좌표_객체를_만든다() {
        BigDecimal x = BigDecimal.valueOf(123.1234);
        BigDecimal y = BigDecimal.valueOf(37.1234);

        Coordinate coordinate = new Coordinate(x, y);

        assertAll(
                () -> assertThat(coordinate.getX()).isEqualTo(x),
                () -> assertThat(coordinate.getY()).isEqualTo(y)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"-180.0123", "180.0123"})
    void 경도_범위를_벗어나면_예외가_발생한다(String x) {
        BigDecimal y = BigDecimal.valueOf(37.1234);

        assertThatThrownBy(() -> new Coordinate(new BigDecimal(x), y))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("경도 값의 크기가 잘못되었습니다.");

    }

    @ParameterizedTest
    @ValueSource(strings = {"-90.0123", "90.0123"})
    void 위도_좌표_범위를_벗어나면_예외가_발생한다(String y) {
        BigDecimal x = BigDecimal.valueOf(123.1234);

        assertThatThrownBy(() -> new Coordinate(x, new BigDecimal(y)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("위도 값의 크기가 잘못되었습니다.");
    }
}