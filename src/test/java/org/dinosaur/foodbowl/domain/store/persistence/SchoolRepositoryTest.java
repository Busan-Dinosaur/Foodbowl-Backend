package org.dinosaur.foodbowl.domain.store.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.util.List;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.vo.SchoolName;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class SchoolRepositoryTest extends PersistenceTest {

    @Autowired
    private SchoolRepository schoolRepository;

    @Test
    void 이름으로_학교를_조회한다() {
        School school = schoolTestPersister.builder().save();

        School findSchool = schoolRepository.findByName(new SchoolName(school.getName())).get();

        assertThat(findSchool.getName()).isEqualTo(school.getName());
    }

    @Test
    void 모든_학교_목록을_조회한다() {
        School schoolA = schoolTestPersister.builder().name("부산대학교").save();
        School schoolB = schoolTestPersister.builder().name("강남대학교").save();

        List<School> result = schoolRepository.findAllByOrderByName();

        assertThat(result).containsExactly(schoolB, schoolA);
    }

    @Test
    void 학교를_저장한다() {
        School school = School.builder()
                .name("부산대학교")
                .x(BigDecimal.valueOf(123.12451))
                .y(BigDecimal.valueOf(37.124125))
                .build();

        School saveSchool = schoolRepository.save(school);

        assertSoftly(softly -> {
            softly.assertThat(saveSchool.getId()).isNotNull();
            softly.assertThat(saveSchool.getName()).isEqualTo(school.getName());
        });
    }
}
