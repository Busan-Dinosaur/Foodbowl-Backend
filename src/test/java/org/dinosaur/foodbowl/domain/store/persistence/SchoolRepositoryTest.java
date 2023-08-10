package org.dinosaur.foodbowl.domain.store.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class SchoolRepositoryTest extends PersistenceTest {

    @Autowired
    private SchoolRepository schoolRepository;

    @Test
    void 학교를_저장한다() {
        School school = generateSchool();

        School saveSchool = schoolRepository.save(school);

        assertAll(
                () -> assertThat(saveSchool.getId()).isNotNull(),
                () -> assertThat(saveSchool.getName()).isEqualTo(school.getName())
        );
    }

    @Test
    void 이름으로_학교를_조회한다() {
        School school = generateSchool();
        schoolRepository.save(school);

        School findSchool = schoolRepository.findByName_Name(school.getName().getName()).get();

        assertThat(findSchool.getName()).isEqualTo(school.getName());
    }

    private School generateSchool() {
        return School.builder()
                .name("부산대학교")
                .x(BigDecimal.valueOf(123.12451))
                .y(BigDecimal.valueOf(37.124125))
                .build();
    }
}
