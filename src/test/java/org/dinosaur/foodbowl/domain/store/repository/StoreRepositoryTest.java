package org.dinosaur.foodbowl.domain.store.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;
import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.store.entity.Address;
import org.dinosaur.foodbowl.domain.store.entity.Store;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StoreRepositoryTest extends RepositoryTest {

    @Autowired
    StoreRepository storeRepository;

    @Test
    @DisplayName("모든 가게를 반환한다.")
    void findAllSuccess() {
        assertThat(storeRepository.findAll()).hasSizeGreaterThanOrEqualTo(0);
    }

    private Store createStore() {
        Address address = createAddress();
        return Store.builder()
                .address(address)
                .storeName("작살치킨")
                .build();
    }

    private Address createAddress() {
        return Address.builder()
                .addressName("서울시 송파구 방이동 방이로 1234")
                .region1depthName("서울시")
                .region2depthName("송파구")
                .region3depthName("방이동")
                .roadName("방이로")
                .mainBuildingNo("1234")
                .subBuildingNo("14층 1400호")
                .undergroundYN("N")
                .buildingName("작살치킨 빌딩")
                .zoneNo("12345")
                .x(BigDecimal.valueOf(127.3437575))
                .y(BigDecimal.valueOf(37.12567))
                .build();
    }

    @Nested
    @DisplayName("findById 메서드는")
    class FindById {

        @Test
        @DisplayName("가게 ID에 해당하는 정보를 가져온다.")
        void findByIdSuccess() {
            Store savedStore = storeRepository.save(createStore());

            Store findStore = storeRepository.findById(savedStore.getId()).get();

            assertThat(findStore).isEqualTo(savedStore);
        }

        @Test
        @DisplayName("가게 ID에 해당하는 가게가 없으면 빈 값을 반환한다.")
        void findOneWithEmptySuccess() {
            storeRepository.save(createStore());

            Optional<Store> store = storeRepository.findById(Long.MAX_VALUE);

            assertThat(store.isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("findByStoreName 메서드는")
    class FindByStoreName {

        @Test
        @DisplayName("가게 이름에 해당하는 가게 정보를 가져온다.")
        void findByStoreNameSuccess() {
            Store savedStore = storeRepository.save(createStore());

            Optional<Store> findStore = storeRepository.findByStoreName(savedStore.getStoreName());

            assertThat(findStore.get()).isEqualTo(savedStore);
        }

        @Test
        @DisplayName("가게 이름에 해당하는 가게가 없으면 빈 값을 반환한다.")
        void findByStoreNameWithEmptySuccess() {
            storeRepository.save(createStore());

            Optional<Store> findStore = storeRepository.findByStoreName("푸드볼");

            assertThat(findStore.isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("findByAddress_AddressName 메서드는")
    class findByAddress_AddressName {

        @Test
        @DisplayName("가게 주소에 해당하는 가게 정보를 가져온다.")
        void findByAddress_AddressNameSuccess() {
            Store savedStore = storeRepository.save(createStore());

            Optional<Store> findStore = storeRepository.findByAddress_AddressName(savedStore.getAddress().getAddressName());

            assertThat(findStore.get()).isEqualTo(savedStore);
        }

        @Test
        @DisplayName("가게 이름에 해당하는 가게가 없으면 빈 값을 반환한다.")
        void findByStoreNameWithEmptySuccess() {
            storeRepository.save(createStore());

            Optional<Store> findStore = storeRepository.findByAddress_AddressName("부산시 금정구 장전동 부산대학로 21");

            assertThat(findStore).isEmpty();
        }
    }
}
