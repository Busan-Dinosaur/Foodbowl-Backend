package org.dinosaur.foodbowl.global.presentation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PositiveListValidator.class)
public @interface PositiveList {

    String message() default "List elements must be positive";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
