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

        private Category category;

        public StoreBuilder category(Category category) {
            this.category = category;
            return this;
        }

        public Store save() {
            return storeRepository.save(
                    Store.builder()
                            .locationId("123124124")
                            .storeName("비비큐 여의도한강공원점")
                            .category(category == null ? categoryRepository.findById(1L) : category)
                            .address(Address.of("서울시 영등포구 여의도동 451", BigDecimal.valueOf(123.23),
                                    BigDecimal.valueOf(35.52)))
                            .storeUrl("http://image.bbq.foodbowl")
                            .phone("02-123-4567")
                            .build()
            );
        }
    }
}
