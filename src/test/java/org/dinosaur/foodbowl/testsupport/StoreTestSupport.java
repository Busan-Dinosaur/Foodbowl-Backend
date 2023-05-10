package org.dinosaur.foodbowl.testsupport;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.entity.Address;
import org.dinosaur.foodbowl.domain.store.entity.Store;
import org.dinosaur.foodbowl.domain.store.repository.StoreRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StoreTestSupport {

    private final StoreRepository storeRepository;

    public StoreBuilder builder() {
        return new StoreBuilder();
    }

    private Address randomAddress() {
        return Address.builder()
                .addressName("address" + UUID.randomUUID())
                .region1depthName("서울특별시")
                .region2depthName("관악구")
                .region3depthName("신림동")
                .x(new BigDecimal(45.59))
                .y(new BigDecimal(18.59))
                .build();
    }

    public final class StoreBuilder {

        private Address address;
        private String storeName;

        public StoreBuilder address(Address address) {
            this.address = address;
            return this;
        }

        public StoreBuilder storeName(String storeName) {
            this.storeName = storeName;
            return this;
        }

        public Store build() {
            return storeRepository.save(
                    Store.builder()
                            .address(address == null ? randomAddress() : address)
                            .storeName(storeName == null ? "name" + UUID.randomUUID() : storeName)
                            .build()
            );
        }
    }
}
