package org.dinosaur.foodbowl.domain.store.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.util.List;
import org.dinosaur.foodbowl.domain.store.application.dto.StoreCreateDto;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.StoreSchool;
import org.dinosaur.foodbowl.domain.store.domain.vo.SchoolName;
import org.dinosaur.foodbowl.domain.store.dto.response.CategoriesResponse;
import org.dinosaur.foodbowl.domain.store.persistence.StoreSchoolRepository;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class StoreServiceTest extends IntegrationTest {

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreSchoolRepository storeSchoolRepository;

    @Test
    void 카테고리_목록을_조회한다() {
        CategoriesResponse response = storeService.getCategories();

        assertThat(response.categories()).hasSize(11);
    }

    @Nested
    class 가게를_조회할_때 {

        @Test
        void 이미_존재하는_가게를_조회한다() {
            StoreCreateDto storeCreateDtoWithoutSchool = generateStoreCreateDto(
                    null,
                    null,
                    null
            );
            Store store = storeService.create(storeCreateDtoWithoutSchool);

            assertThat(storeService.findByLocationId(store.getLocationId())).isPresent();
        }

        @Test
        void 존재하지_않는_가게를_조회한다() {
            String locationId = String.valueOf(Long.MAX_VALUE);

            assertThat(storeService.findByLocationId(locationId)).isEmpty();
        }

        @Test
        void ID로_조회한다() {
            StoreCreateDto storeCreateDtoWithoutSchool = generateStoreCreateDto(
                    null,
                    null,
                    null
            );
            Store store = storeService.create(storeCreateDtoWithoutSchool);

            Store findStore = storeService.findById(store.getId());

            assertThat(findStore).isEqualTo(store);
        }

        @Test
        void 존재하지_않는_ID로_조회하면_예외가_발생한다() {
            assertThatThrownBy(() -> storeService.findById(Long.MAX_VALUE))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("일치하는 가게를 찾을 수 없습니다.");
        }
    }

    @Nested
    class 가게를_생성할_때 {

        @Test
        void 학교_없이_생성한다() {
            StoreCreateDto storeCreateDtoWithoutSchool = generateStoreCreateDto(
                    null,
                    null,
                    null
            );

            Store store = storeService.create(storeCreateDtoWithoutSchool);

            assertSoftly(softly -> {
                softly.assertThat(store.getId()).isNotNull();
                softly.assertThat(store.getLocationId()).isEqualTo(storeCreateDtoWithoutSchool.locationId());
                softly.assertThat(store.getStoreName()).isEqualTo(storeCreateDtoWithoutSchool.storeName());
                softly.assertThat(store.getCategory().getCategoryType().name())
                        .isEqualTo(storeCreateDtoWithoutSchool.category());
            });
        }

        @Test
        void 학교와_함께_생성한다() {
            StoreCreateDto storeCreateDtoWithSchool = generateStoreCreateDto(
                    "부산대학교",
                    BigDecimal.valueOf(123.12),
                    BigDecimal.valueOf(37.1234)
            );

            Store store = storeService.create(storeCreateDtoWithSchool);
            List<StoreSchool> storeSchools = storeSchoolRepository.findAllBySchool_Name(new SchoolName("부산대학교"));
            List<Store> stores = storeSchools.stream()
                    .map(StoreSchool::getStore)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(store.getId()).isNotNull();
                softly.assertThat(store.getStoreName()).isEqualTo(storeCreateDtoWithSchool.storeName());
                softly.assertThat(store.getCategory().getCategoryType().name())
                        .isEqualTo(storeCreateDtoWithSchool.category());
                softly.assertThat(stores).contains(store);
            });
        }

        @Test
        void 이미_존재하는_학교인_경우도_학교와_함께_생성한다() {
            StoreCreateDto storeCreateDtoWithSchool = generateStoreCreateDto(
                    "부산대학교",
                    BigDecimal.valueOf(123.12),
                    BigDecimal.valueOf(37.1234)
            );
            Store store1 = storeService.create(storeCreateDtoWithSchool);
            StoreCreateDto nextStoreCreateDto = new StoreCreateDto(
                    "9999999",
                    "용용선생 선릉점",
                    "중식",
                    "서울시 강남구 선릉로 424번길 2323",
                    BigDecimal.valueOf(123.1232134),
                    BigDecimal.valueOf(37.45433545),
                    "http://images2.foodbowl",
                    "02-2141-4567",
                    "부산대학교",
                    BigDecimal.valueOf(123.12),
                    BigDecimal.valueOf(37.1234));

            Store store2 = storeService.create(nextStoreCreateDto);
            List<StoreSchool> storeSchools = storeSchoolRepository.findAllBySchool_Name(new SchoolName("부산대학교"));
            List<Store> stores = storeSchools.stream()
                    .map(StoreSchool::getStore)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(stores).contains(store1, store2);
                softly.assertThat(stores).hasSize(2);
            });
        }

        @Test
        void 이미_존재하는_가게이면_예외가_발생한다() {
            StoreCreateDto storeCreateDtoWithoutSchool = generateStoreCreateDto(
                    null,
                    null,
                    null
            );
            storeService.create(storeCreateDtoWithoutSchool);

            assertThatThrownBy(() -> storeService.create(storeCreateDtoWithoutSchool))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 존재하는 가게입니다.");
        }

        @ParameterizedTest
        @ValueSource(strings = {"!부산대학교", "@서울대학교@", "+연세대학교-", "!@#!$"})
        void 학교_이름이_형식과_다르면_예외가_발생한다(String schoolName) {
            StoreCreateDto storeCreateDto = new StoreCreateDto(
                    "1234567",
                    "농민백암순대",
                    "한식",
                    "서울시 강남구 선릉로 14번길 245",
                    BigDecimal.valueOf(123.124),
                    BigDecimal.valueOf(37.4545),
                    "http://images.foodbowl",
                    "02-123-4567",
                    schoolName,
                    null,
                    null
            );

            assertThatThrownBy(() -> storeService.create(storeCreateDto))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("학교 이름 형식이 잘못되었습니다.");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"미국식", "한국식", "학식", "급식"})
        void 카테고리_타입이_존재하지_않으면_예외가_발생한다(String category) {
            StoreCreateDto storeCreateDto = new StoreCreateDto(
                    "21415511",
                    "농민백암순대",
                    category,
                    "서울시 강남구 선릉로 14번길 245",
                    BigDecimal.valueOf(123.124),
                    BigDecimal.valueOf(37.4545),
                    "http://images.foodbowl",
                    "02-123-4567",
                    null,
                    null,
                    null
            );

            assertThatThrownBy(() -> storeService.create(storeCreateDto))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("일치하는 카테고리를 찾을 수 없습니다.");
        }
    }

    private StoreCreateDto generateStoreCreateDto(String schoolName, BigDecimal schoolX, BigDecimal schoolY) {
        return new StoreCreateDto(
                "12314535",
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
