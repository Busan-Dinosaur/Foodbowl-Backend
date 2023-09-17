package org.dinosaur.foodbowl.global.presentation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class PositiveListValidator implements ConstraintValidator<PositiveList, List<Long>> {

    @Override
    public boolean isValid(List<Long> values, ConstraintValidatorContext context) {
        if (values == null) {
            return true;
        }

        return values.stream()
                .allMatch(this::isPositive);
    }

    private boolean isPositive(Long value) {
        return value > 0;
    }
}
