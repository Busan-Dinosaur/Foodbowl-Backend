package org.dinosaur.foodbowl.domain.store.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.store.dto.StoreRequest;
import org.dinosaur.foodbowl.domain.store.dto.StoreResponse;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StoreServiceTest extends IntegrationTest {

    @Autowired
    StoreService storeService;

    @Test
    @DisplayName("가게를 저장한다.")
    void saveSuccess() {
        StoreRequest storeRequest = createRequest("국민연금공단 구내식당", "서울시 송파구 올림픽로 123");

        StoreResponse storeResponse = storeService.save(storeRequest);

        assertAll(
                () -> assertThat(storeResponse.getId()).isPositive(),
                () -> assertThat(storeResponse.getStoreName()).isEqualTo(storeRequest.getStoreName()),
                () -> assertThat(storeResponse.getAddressName()).isEqualTo(storeRequest.getAddressName()),
                () -> assertThat(storeResponse.getRegion1depthName()).isEqualTo(storeRequest.getRegion1depthName()),
                () -> assertThat(storeResponse.getRegion2depthName()).isEqualTo(storeRequest.getRegion2depthName()),
                () -> assertThat(storeResponse.getRegion3depthName()).isEqualTo(storeRequest.getRegion3depthName()),
                () -> assertThat(storeResponse.getRoadName()).isEqualTo(storeRequest.getRoadName()),
                () -> assertThat(storeResponse.getUndergroundYN()).isEqualTo(storeRequest.getUndergroundYN()),
                () -> assertThat(storeResponse.getMainBuildingNo()).isEqualTo(storeRequest.getMainBuildingNo()),
                () -> assertThat(storeResponse.getSubBuildingNo()).isEqualTo(storeRequest.getSubBuildingNo()),
                () -> assertThat(storeResponse.getBuildingName()).isEqualTo(storeRequest.getBuildingName()),
                () -> assertThat(storeResponse.getZoneNo()).isEqualTo(storeRequest.getZoneNo()),
                () -> assertThat(storeResponse.getX()).isEqualTo(storeRequest.getX()),
                () -> assertThat(storeResponse.getY()).isEqualTo(storeRequest.getY())
        );
    }

    @Test
    @DisplayName("등록된 가게가 존재하는 경우 등록 요청이 오는 발생하면 예외가 발생한다.")
    void saveFail() {
        storeService.save(createRequest("국민연금공단 구내식당", "서울시 송파구 올림픽로 123"));

        Assertions.assertThatThrownBy(() -> storeService.save(createRequest("국민연금공단 구내식당", "서울시 송파구 올림픽로 123")))
                .isInstanceOf(FoodbowlException.class)
                .hasMessageContaining("이미 등록된 가게입니다.");
    }

    @Test
    @DisplayName("ID에 해당하는 가게 정보를 가져온다.")
    void findOneSuccess() {
        Long savedId = storeService.save(createRequest("국민연금공단 구내식당", "서울시 송파구 올림픽로 123")).getId();

        StoreResponse findStore = storeService.findOne(savedId);

        assertThat(findStore.getId()).isEqualTo(savedId);
    }

    @Test
    @DisplayName("ID에 해당하는 가게가 없으면 예외가 발생한다.")
    void findOneFail() {
        storeService.save(createRequest("국민연금공단 구내식당", "서울시 송파구 올림픽로 123"));

        Assertions.assertThatThrownBy(() -> storeService.findOne(Long.MAX_VALUE))
                .isInstanceOf(FoodbowlException.class)
                .hasMessageContaining("일치하는 가게를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("저장된 모든 가게를 가져온다.")
    void findAllSuccess() {
        int initialSize = storeService.findAll().size();

        storeService.save(createRequest("국민연금공단 구내식당", "서울시 송파구 올림픽로 123"));
        storeService.save(createRequest("시부야돈까스", "서울시 송파구 장미상가로 424"));

        List<StoreResponse> storeResponses = storeService.findAll();

        assertThat(storeResponses).hasSize(initialSize + 2);
    }

    private StoreRequest createRequest(String storeName, String addressName) {
        return new StoreRequest(
                storeName,
                addressName,
                "서울시",
                "송파구",
                "신천동",
                "연금공단로",
                "N",
                "123",
                "1층 101호",
                "국민연금공단 송파지점",
                "12345",
                BigDecimal.valueOf(127.3435356),
                BigDecimal.valueOf(37.12314545)
        );
    }
}
