package org.dinosaur.foodbowl.global.util;

import java.math.BigDecimal;
import org.dinosaur.foodbowl.domain.store.exception.CoordinateExceptionType;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;

public class VerifiedCoordinate {

    private static final BigDecimal MIN_X_VALUE = BigDecimal.valueOf(-180);
    private static final BigDecimal MAX_X_VALUE = BigDecimal.valueOf(180);
    private static final BigDecimal MIN_Y_VALUE = BigDecimal.valueOf(-90);
    private static final BigDecimal MAX_Y_VALUE = BigDecimal.valueOf(90);

    private BigDecimal x;
    private BigDecimal y;

    public VerifiedCoordinate(BigDecimal x, BigDecimal y) {
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

    public double getX() {
        return x.doubleValue();
    }

    public double getY() {
        return y.doubleValue();
    }
}
