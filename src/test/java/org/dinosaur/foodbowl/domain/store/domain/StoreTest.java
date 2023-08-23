package org.dinosaur.foodbowl.domain.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.dinosaur.foodbowl.domain.store.domain.vo.Address;
import org.dinosaur.foodbowl.domain.store.domain.vo.CategoryType;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StoreTest {

    @Test
    void 가게를_생성한다() {
        Category category = Category.builder()
                .id(1L)
                .categoryType(CategoryType.카페)
                .build();
        Address address = Address.builder()
                .addressName("서울시 강남구 선릉로 14번길 245")
                .region1depthName("서울시")
                .region2depthName("강남구")
                .region3depthName("선릉로")
                .roadName("14번길 245")
                .x(BigDecimal.valueOf(123.124))
                .y(BigDecimal.valueOf(37.4545))
                .build();
        Store store = Store.builder()
                .category(category)
                .storeName("농민백암순대")
                .address(address)
                .storeUrl("http://foodbowl.com")
                .phone("02-123-4567")
                .build();

        assertThat(store.getStoreName()).isEqualTo("농민백암순대");
    }
}
