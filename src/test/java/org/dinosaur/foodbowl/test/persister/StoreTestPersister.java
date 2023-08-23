package org.dinosaur.foodbowl.test.persister;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.domain.Category;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.vo.Address;
import org.dinosaur.foodbowl.domain.store.persistence.CategoryRepository;
import org.dinosaur.foodbowl.domain.store.persistence.StoreRepository;

@RequiredArgsConstructor
@Persister
public class StoreTestPersister {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;

    public StoreBuilder builder() {
        return new StoreBuilder();
    }

    public class StoreBuilder {

        private String locationId;
        private String storeName;
        private Category category;
        private Address address;
        private String storeUrl;
        private String phone;

        public StoreBuilder locationId(String locationId) {
            this.locationId = locationId;
            return this;
        }

        public StoreBuilder storeName(String storeName) {
            this.storeName = storeName;
            return this;
        }

        public StoreBuilder category(Category category) {
            this.category = category;
            return this;
        }

        public StoreBuilder address(Address address) {
            this.address = address;
            return this;
        }

        public StoreBuilder storeUrl(String storeUrl) {
            this.storeUrl = storeUrl;
            return this;
        }

        public StoreBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Store save() {
            Store store = Store.builder()
                    .locationId(locationId == null ? "123124124" : locationId)
                    .storeName(storeName == null ? "비비큐 여의도한강공원점" : storeName)
                    .category(category == null ? categoryRepository.findById(1L) : category)
                    .address(address == null ? Address.of(
                            "서울시 영등포구 여의도동 451",
                            BigDecimal.valueOf(123.23),
                            BigDecimal.valueOf(35.52)
                    ) : address)
                    .storeUrl(storeUrl == null ? "http://image.bbq.foodbowl" : storeUrl)
                    .phone(phone == null ? "02-123-4567" : phone)
                    .build();
            return storeRepository.save(store);
        }
    }
}
