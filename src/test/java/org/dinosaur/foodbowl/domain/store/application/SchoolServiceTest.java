package org.dinosaur.foodbowl.domain.store.application;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
class SchoolServiceTest extends IntegrationTest {

    @Autowired
    private SchoolService schoolService;

    @Nested
    class 학교를_저장할_때_ {

        @Test
        void 정상적으로_저장한다() {
            School school = schoolService.save("부산대학교", BigDecimal.valueOf(123.1234), BigDecimal.valueOf(37.12421));

            assertThat(school).isNotNull();
        }

        @Test
        void 이미_학교가_존재하면_예외_발생() {
            String name = "부산대학교";
            BigDecimal x = BigDecimal.valueOf(123.1234);
            BigDecimal y = BigDecimal.valueOf(37.12421);
            schoolService.save(name, x, y);

            assertThatThrownBy(() -> schoolService.save(name, x, y))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 존재하는 학교입니다.");
        }
    }

    @Nested
    class 학교_조회_ {

        @Test
        void 존재하는_학교로_조회() {
            String name = "부산대학교";
            BigDecimal x = BigDecimal.valueOf(123.1234);
            BigDecimal y = BigDecimal.valueOf(37.12421);
            schoolService.save(name, x, y);

            assertThat(schoolService.findByName(name)).isPresent();
        }

        @Test
        void 존재하지_않는_학교로_조회() {
            assertThat(schoolService.findByName("우테코대학교")).isEmpty();
        }
    }
}
