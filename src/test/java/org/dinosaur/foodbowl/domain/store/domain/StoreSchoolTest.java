package org.dinosaur.foodbowl.domain.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StoreSchoolTest {

    @Test
    void 가게_학교_정보를_생성한다() {
        Store store = Store.builder()
                .storeName("농민백암순대")
                .storeUrl("http://foodbowl.com")
                .phone("02-123-4567")
                .build();
        School school = School.builder()
                .name("부산대학교")
                .x(BigDecimal.valueOf(124.1234))
                .y(BigDecimal.valueOf(34.545))
                .build();
        StoreSchool storeSchool = StoreSchool.builder()
                .store(store)
                .school(school)
                .build();

        assertThat(storeSchool.getSchool()).isEqualTo(school);
    }
}
