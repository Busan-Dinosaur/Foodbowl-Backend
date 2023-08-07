package org.dinosaur.foodbowl.domain.store.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.StoreSchool;
import org.dinosaur.foodbowl.domain.store.dto.StoreCreateDto;
import org.dinosaur.foodbowl.domain.store.persistence.StoreSchoolRepository;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class StoreServiceTest extends IntegrationTest {

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreSchoolRepository storeSchoolRepository;

    @Nested
    class 가게를_생성할_때_ {

        @Test
        void 학교_없이_생성한다() {
            StoreCreateDto storeCreateDtoWithoutSchool = generateStoreCreateDto(null, null, null);

            Store store = storeService.create(storeCreateDtoWithoutSchool);

            assertAll(
                    () -> assertThat(store.getId()).isNotNull(),
                    () -> assertThat(store.getStoreName()).isEqualTo(storeCreateDtoWithoutSchool.storeName()),
                    () -> assertThat(store.getCategory().getCategoryType().name()).isEqualTo(storeCreateDtoWithoutSchool.category())
            );
        }

        @Test
        void 학교와_함께_생성한다() {
            StoreCreateDto storeCreateDtoWithSchool = generateStoreCreateDto("부산대학교", BigDecimal.valueOf(123.12),
                    BigDecimal.valueOf(37.1234));

            Store store = storeService.create(storeCreateDtoWithSchool);
            List<StoreSchool> storeSchools = storeSchoolRepository.findAllBySchoolName("부산대학교");
            List<Store> stores = storeSchools.stream()
                    .map(StoreSchool::getStore)
                    .toList();

            assertAll(
                    () -> assertThat(store.getId()).isNotNull(),
                    () -> assertThat(store.getStoreName()).isEqualTo(storeCreateDtoWithSchool.storeName()),
                    () -> assertThat(store.getCategory().getCategoryType().name()).isEqualTo(storeCreateDtoWithSchool.category()),
                    () -> assertThat(stores).contains(store)
            );
        }

        @Test
        void 이미_존재하는_가게이면_예외_발생() {
            StoreCreateDto storeCreateDtoWithoutSchool = generateStoreCreateDto(null, null, null);
            storeService.create(storeCreateDtoWithoutSchool);

            assertThatThrownBy(() -> storeService.create(storeCreateDtoWithoutSchool))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 존재하는 가게입니다.");
        }
    }

    @Test
    void 이미_존재하는_가게_조회() {
        StoreCreateDto storeCreateDtoWithoutSchool = generateStoreCreateDto(null, null, null);
        Store store = storeService.create(storeCreateDtoWithoutSchool);

        assertThat(storeService.findByAddress(store.getAddress().getAddressName())).isPresent();
    }

    @Test
    void 존재하지_않는_가게_조회() {
        assertThat(storeService.findByAddress("부산시 금정구 부산대학로 123번길 12")).isEmpty();
    }

    private StoreCreateDto generateStoreCreateDto(String schoolName, BigDecimal schoolX, BigDecimal schoolY) {
        return new StoreCreateDto(
                "농민백암순대",
                "한식",
                "서울시 강남구 선릉로 14번길 245",
                BigDecimal.valueOf(123.124),
                BigDecimal.valueOf(37.4545),
                "http://images.foodbowl",
                "02-123-4567",
                schoolName,
                schoolX,
                schoolY
        );
    }
}
