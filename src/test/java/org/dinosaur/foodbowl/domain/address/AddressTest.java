package org.dinosaur.foodbowl.domain.address;

import org.dinosaur.foodbowl.domain.store.entity.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AddressTest {

    @Test
    @DisplayName("주소 객체가 생성된다.")
    void createAddress() {
        Address address = Address.builder()
                .addressName("서울시 송파구 올림픽로 473")
                .region1depthName("서울시")
                .region2depthName("송파구")
                .region3depthName("올림픽로")
                .roadName("올림픽로")
                .mainBuildingNo("473")
                .subBuildingNo("14층 1400호")
                .undergroundYN("N")
                .buildingName("루터회관")
                .zoneNo("12345")
                .x(BigDecimal.valueOf(127.3437575))
                .y(BigDecimal.valueOf(37.12567))
                .build();

        assertThat(address).isNotNull();
    }
}
