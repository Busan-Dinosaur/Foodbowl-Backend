package org.dinosaur.foodbowl.domain.store.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.vo.SchoolName;
import org.dinosaur.foodbowl.global.util.PointUtils;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class SchoolRepositoryTest extends PersistenceTest {

    @Autowired
    private SchoolRepository schoolRepository;

    @Nested
    class ID로_학교_조회_시 {

        @Test
        void 학교가_존재하면_학교를_반환한다() {
            School school = schoolTestPersister.builder().save();

            Optional<School> result = schoolRepository.findById(school.getId());

            assertThat(result).isPresent();
        }

        @Test
        void 학교가_존재하지_않으면_빈_값을_반환한다() {
            Optional<School> result = schoolRepository.findById(-1L);

            assertThat(result).isNotPresent();
        }
    }

    @Test
    void 학교_이름으로_학교를_조회한다() {
        School school = schoolTestPersister.builder().save();

        School findSchool = schoolRepository.findByName(new SchoolName(school.getName())).get();

        assertThat(findSchool.getName()).isEqualTo(school.getName());
    }

    @Test
    void 모든_학교_목록을_이름순으로_정렬하여_조회한다() {
        School schoolA = schoolTestPersister.builder().name("부산대학교").save();
        School schoolB = schoolTestPersister.builder().name("강남대학교").save();

        List<School> result = schoolRepository.findAllByOrderByName();

        assertThat(result).containsExactly(schoolB, schoolA);
    }

    @Test
    void 학교를_저장한다() {
        School school = School.builder()
                .name("부산대학교")
                .addressName("부산광역시 금정구 부산대학로63번길 2")
                .coordinate(PointUtils.generate(BigDecimal.valueOf(123.12451), BigDecimal.valueOf(37.124125)))
                .build();

        School saveSchool = schoolRepository.save(school);

        assertSoftly(softly -> {
            softly.assertThat(saveSchool.getId()).isNotNull();
            softly.assertThat(saveSchool.getName()).isEqualTo(school.getName());
        });
    }
}
