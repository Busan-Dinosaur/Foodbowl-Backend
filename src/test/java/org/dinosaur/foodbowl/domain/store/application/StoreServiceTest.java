package org.dinosaur.foodbowl.domain.store.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.dinosaur.foodbowl.exception.ErrorStatus.STORE_DUPLICATED;
import static org.dinosaur.foodbowl.exception.ErrorStatus.STORE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.store.dto.StoreRequest;
import org.dinosaur.foodbowl.domain.store.dto.StoreResponse;
import org.dinosaur.foodbowl.domain.store.entity.Address;
import org.dinosaur.foodbowl.domain.store.entity.Store;
import org.dinosaur.foodbowl.exception.FoodbowlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StoreServiceTest extends IntegrationTest {

    @Autowired
    StoreService storeService;

    @Test
    @DisplayName("저장된 모든 가게를 가져온다.")
    void findAllSuccess() {
        int initialSize = storeService.findAll().size();

        storeService.save(createRequest("국민연금공단 구내식당", "서울시 송파구 올림픽로 123"));
        storeService.save(createRequest("시부야돈까스", "서울시 송파구 장미상가로 424"));

        List<StoreResponse> storeResponses = storeService.findAll();

        assertThat(storeResponses).hasSize(initialSize + 2);
    }

    private Address createAddress() {
        return Address.builder()
                .addressName("서울시 송파구 신천동 1542")
                .region1depthName("서울시")
                .region2depthName("송파구")
                .region3depthName("신천동")
                .roadName("연금공단로")
                .x(BigDecimal.valueOf(127.3435356))
                .y(BigDecimal.valueOf(37.12314545))
                .build();
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

    @Nested
    @DisplayName("save 메서드는 ")
    class Save {

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
                    () -> assertThat(storeResponse.getX()).isEqualTo(storeRequest.getX()),
                    () -> assertThat(storeResponse.getY()).isEqualTo(storeRequest.getY())
            );
        }

        @Test
        @DisplayName("등록된 가게가 존재하는 경우 등록 요청이 오는 발생하면 예외가 발생한다.")
        void saveFail() {
            storeService.save(createRequest("국민연금공단 구내식당", "서울시 송파구 올림픽로 123"));

            assertThatThrownBy(() -> storeService.save(createRequest("국민연금공단 구내식당", "서울시 송파구 올림픽로 123")))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessageContaining(STORE_DUPLICATED.getMessage());
        }
    }

    @Nested
    @DisplayName("find 메서드는 ")
    class Find {

        @Test
        @DisplayName("ID에 해당하는 가게 정보를 가져온다.")
        void findOneSuccess() {
            Long savedId = storeTestSupport.builder().build().getId();

            StoreResponse findStore = storeService.findOne(savedId);

            assertThat(findStore.getId()).isEqualTo(savedId);
        }

        @Test
        @DisplayName("ID에 해당하는 가게가 없으면 예외가 발생한다.")
        void findOneFail() {
            assertThatThrownBy(() -> storeService.findOne(-1L))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessageContaining(STORE_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("findByAddress 메서드는 ")
    class FindByAddress {

        @Test
        @DisplayName("주소에 해당하는 가게 정보를 가져온다.")
        void findByAddressSuccess() {
            Address address = createAddress();
            Store store = storeTestSupport.builder().address(address).storeName("맥도날드 잠실점").build();

            StoreResponse findStoreResponse = storeService.findByAddress(address.getAddressName());

            assertAll(
                    () -> assertThat(findStoreResponse.getId()).isEqualTo(store.getId()),
                    () -> assertThat(findStoreResponse.getAddressName()).isEqualTo(store.getAddress().getAddressName())
            );
        }

        @Test
        @DisplayName("주소에 해당하는 가게 정보를 가져온다.")
        void findByAddressFail() {
            Address address = createAddress();
            storeTestSupport.builder().address(address).storeName("맥도날드 잠실점").build();

            assertThatThrownBy(() -> storeService.findByAddress("제주시 송파구 신천동"))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessageContaining(STORE_NOT_FOUND.getMessage());
        }
    }
}
