package org.dinosaur.foodbowl.domain.review.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import org.dinosaur.foodbowl.global.presentation.PositiveListValidator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class PositiveListValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @Nested
    class Is_Valid_메서드는 {

        @Test
        void 리스트_원소가_모두_양수인_경우_true_반환한다() {
            List<Long> numbers = List.of(1L, 2L, 3L);

            PositiveListValidator positiveListValidator = new PositiveListValidator();

            assertThat(positiveListValidator.isValid(numbers, context)).isTrue();
        }

        @Test
        void 리스트_원소에_음수가_포함된_경우_false_반환한다() {
            List<Long> numbers = List.of(1L, -2L, 3L);

            PositiveListValidator positiveListValidator = new PositiveListValidator();

            assertThat(positiveListValidator.isValid(numbers, context)).isFalse();
        }
    }
}
