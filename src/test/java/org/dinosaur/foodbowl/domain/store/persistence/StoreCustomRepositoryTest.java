package org.dinosaur.foodbowl.domain.store.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.util.List;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.vo.Address;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreSearchResponse;
import org.dinosaur.foodbowl.global.util.PointUtils;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class StoreCustomRepositoryTest extends PersistenceTest {

    @Autowired
    private StoreCustomRepository storeCustomRepository;

    @Test
    void 이름이_포함된_가게를_가까운_순으로_조회한다() {
        String name = "김밥";
        double x = 124.5135;
        double y = 36.1234;
        Store storeA = storeTestPersister.builder()
                .address(createAddress(x + 0.05, y))
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

        List<StoreSearchResponse> responses = storeCustomRepository.search(name, x, y, 10);

        List<Long> responseStoreIds = responses.stream()
                .map(StoreSearchResponse::getStoreId)
                .toList();
        assertSoftly(softly -> {
            assertThat(responseStoreIds).containsExactly(storeB.getId(), storeD.getId(), storeA.getId());
            assertThat(responseStoreIds).hasSize(3);
            assertThat(responseStoreIds).doesNotContain(storeC.getId());
        });
    }

    private Address createAddress(double x, double y) {
        return Address.of(
                "서울시 서초구 방배동 1234",
                PointUtils.generate(new BigDecimal(x), new BigDecimal(y))
        );
    }
}
