package org.dinosaur.foodbowl.domain.store.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.dinosaur.foodbowl.PersistenceTest;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.vo.Address;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class StoreRepositoryTest extends PersistenceTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void 가게를_저장한다() {
        Store store = generateStore();

        Store saveStore = storeRepository.save(store);

        assertThat(saveStore.getId()).isNotNull();
    }

    @Test
    void 주소로_가게를_조회한다() {
        Store store = generateStore();
        Store saveStore = storeRepository.save(store);

        Store findStore = storeRepository.findByAddress_AddressName(saveStore.getAddress().getAddressName()).get();

        assertThat(findStore).isEqualTo(saveStore);
    }

    private Store generateStore() {
        return Store.builder()
                .storeName("비비큐 여의도한강공원점")
                .category(categoryRepository.findById(1L))
                .address(Address.of("서울시 영등포구 여의도동 451", BigDecimal.valueOf(123.23), BigDecimal.valueOf(35.52)))
                .storeUrl("http://image.bbq.foodbowl")
                .phone("02-123-4567")
                .build();
    }
}