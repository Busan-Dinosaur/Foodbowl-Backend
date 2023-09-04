package org.dinosaur.foodbowl.domain.store.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.store.exception.CoordinateExceptionType;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coordinate {

    private static final BigDecimal MIN_X_VALUE = BigDecimal.valueOf(-180);
    private static final BigDecimal MAX_X_VALUE = BigDecimal.valueOf(180);
    private static final BigDecimal MIN_Y_VALUE = BigDecimal.valueOf(-90);
    private static final BigDecimal MAX_Y_VALUE = BigDecimal.valueOf(90);

    @NotNull
    @Column(name = "x", updatable = false)
    private BigDecimal x;

    @NotNull
    @Column(name = "y", updatable = false)
    private BigDecimal y;

    public Coordinate(BigDecimal x, BigDecimal y) {
        validateX(x);
        validateY(y);
        this.x = x;
        this.y = y;
    }

    private void validateX(BigDecimal x) {
        if (x.compareTo(MIN_X_VALUE) < 0 || x.compareTo(MAX_X_VALUE) > 0) {
            throw new InvalidArgumentException(CoordinateExceptionType.INVALID_X);
        }
    }

    private void validateY(BigDecimal y) {
        if (y.compareTo(MIN_Y_VALUE) < 0 || y.compareTo(MAX_Y_VALUE) > 0) {
            throw new InvalidArgumentException(CoordinateExceptionType.INVALID_Y);
        }
    }

}
