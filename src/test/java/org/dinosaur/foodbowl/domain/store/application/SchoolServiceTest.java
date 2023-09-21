package org.dinosaur.foodbowl.domain.store.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.dto.response.SchoolsResponse;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class SchoolServiceTest extends IntegrationTest {

    @Autowired
    private SchoolService schoolService;

    @Nested
    class 학교_조회_시 {

        @Test
        void 등록된_학교라면_학교를_조회한다() {
            String name = "부산대학교";
            String addressName = "부산광역시 금정구 부산대학로63번길 2";
            BigDecimal x = BigDecimal.valueOf(123.1234);
            BigDecimal y = BigDecimal.valueOf(37.12421);
            schoolService.save(name, addressName, x, y);

            assertThat(schoolService.findByName(name)).isPresent();
        }

        @Test
        void 등록되지_않은_학교라면_학교가_조회되지_않는다() {
            assertThat(schoolService.findByName("우테코대학교")).isEmpty();
        }
    }

    @Test
    void 모든_학교_목록을_이름순으로_조회한다() {
        School schoolA = schoolTestPersister.builder().name("부산대학교").save();
        School schoolB = schoolTestPersister.builder().name("강남대학교").save();

        SchoolsResponse response = schoolService.getSchools();

        assertThat(response).usingRecursiveComparison().isEqualTo(SchoolsResponse.from(List.of(schoolB, schoolA)));
    }

    @Nested
    class 학교_저장_시 {

        @Test
        void 정상적인_요청이라면_학교를_저장한다() {
            School school = schoolService.save(
                    "부산대학교",
                    "부산광역시 금정구 부산대학로63번길 2",
                    BigDecimal.valueOf(123.1234),
                    BigDecimal.valueOf(37.12421)
            );

            assertThat(school.getId()).isNotNull();
        }

        @Test
        void 이미_등록된_학교라면_예외를_던진다() {
            String name = "부산대학교";
            String addressName = "부산광역시 금정구 부산대학로63번길 2";
            BigDecimal x = BigDecimal.valueOf(123.1234);
            BigDecimal y = BigDecimal.valueOf(37.12421);
            schoolService.save(name, addressName, x, y);

            assertThatThrownBy(() -> schoolService.save(name, addressName, x, y))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 존재하는 학교입니다.");
        }
    }
}
