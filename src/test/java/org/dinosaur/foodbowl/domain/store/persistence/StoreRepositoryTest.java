package org.dinosaur.foodbowl.domain.store.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.vo.Address;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class StoreRepositoryTest extends PersistenceTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void 장소_ID로_가게를_조회한다() {
        Store store = storeTestPersister.builder().save();

        assertThat(storeRepository.findByLocationId(store.getLocationId())).isPresent();
    }

    @Test
    void 가게_ID로_가게를_조회한다() {
        Store store = storeTestPersister.builder().save();

        assertThat(storeRepository.findById(store.getId())).isPresent();
    }

    @Test
    void 가게를_저장한다() {
        Store store = Store.builder()
                .locationId("1412414")
                .storeName("비비큐 여의도한강공원점")
                .category(categoryRepository.findById(1L))
                .address(Address.of(
                        "서울시 영등포구 여의도동 451",
                        BigDecimal.valueOf(123.23),
                        BigDecimal.valueOf(35.52))
                )
                .storeUrl("http://image.bbq.foodbowl")
                .phone("02-123-4567")
                .build();

        Store saveStore = storeRepository.save(store);

        assertThat(saveStore.getId()).isNotNull();
    }
}
