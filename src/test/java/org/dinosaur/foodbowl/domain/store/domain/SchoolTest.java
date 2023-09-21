package org.dinosaur.foodbowl.domain.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SchoolTest {

    @Test
    void 학교를_생성한다() {
        School school = School.builder()
                .name("부산대학교")
                .addressName("부산광역시 금정구 부산대학로63번길 2")
                .x(BigDecimal.valueOf(124.1234))
                .y(BigDecimal.valueOf(34.545))
                .build();

        assertThat(school.getName()).isEqualTo("부산대학교");
    }
}
