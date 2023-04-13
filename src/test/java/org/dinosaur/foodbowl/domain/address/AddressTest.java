package org.dinosaur.foodbowl.domain.address;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.domain.store.entity.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AddressTest {

    @Test
    @DisplayName("주소 객체가 생성된다.")
    void createAddress() {
        Address address = Address.builder()
                .addressName("서울시 송파구 올림픽로 473")
                .region1DepthName("서울시")
                .region2DepthName("송파구")
                .region3DepthName("올림픽로")
                .roadName("올림픽로")
                .mainBuildingNo("473")
                .subBuildingNo("14층 1400호")
                .undergroundYN("N")
                .buildingName("루터회관")
                .zoneNo("12345")
                .latitude(37.12567)
                .longitude(127.3437575)
                .build();

        assertThat(address).isNotNull();
    }

}
