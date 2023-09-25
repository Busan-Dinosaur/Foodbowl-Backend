package org.dinosaur.foodbowl.domain.store.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.util.List;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.vo.Address;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreSearchQueryResponse;
import org.dinosaur.foodbowl.global.util.PointUtils;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class StoreRepositoryTest extends PersistenceTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Nested
    class 가게를_조회할_때 {

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
        void 이름이_포함된_가게를_가까운_순으로_조회한다() {
            String name = "김밥";
            double x = 124.5135;
            double y = 36.1234;
            Store storeA = storeTestPersister.builder()
                    .address(createAddress(x + 0.01, y))
                    .storeName("김밥천국")
                    .save();
            Store storeB = storeTestPersister.builder()
                    .address(createAddress(x + 0.02, y))
                    .storeName("김밥나라")
                    .save();
            Store storeC = storeTestPersister.builder()
                    .address(createAddress(x + 0.03, y))
                    .storeName("꺼벙이분식")
                    .save();
            Store storeD = storeTestPersister.builder()
                    .address(createAddress(x + 0.04, y))
                    .storeName("김밥세상").save();

            List<StoreSearchQueryResponse> searchResponses = storeRepository.search(name, x, y, 10);

            List<Long> responseStoreIds = searchResponses.stream()
                    .map(StoreSearchQueryResponse::getStoreId)
                    .toList();
            assertSoftly(softly -> {
                assertThat(responseStoreIds).containsExactly(storeA.getId(), storeB.getId(), storeD.getId());
                assertThat(responseStoreIds).doesNotContain(storeC.getId());
            });
        }
    }

    @Test
    void 가게를_저장한다() {
        Store store = Store.builder()
                .locationId("1412414")
                .storeName("비비큐 여의도한강공원점")
                .category(categoryRepository.findById(1L))
                .address(createAddress(125.1241, 34.125152))
                .storeUrl("http://image.bbq.foodbowl")
                .phone("02-123-4567")
                .build();

        Store saveStore = storeRepository.save(store);

        assertThat(saveStore.getId()).isNotNull();
    }

    private Address createAddress(double x, double y) {
        return Address.of(
                "서울시 서초구 방배동 1234",
                PointUtils.generate(new BigDecimal(x), new BigDecimal(y))
        );
    }
}
