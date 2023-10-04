package org.dinosaur.foodbowl.domain.store.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.application.dto.MapCoordinateBoundDto;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.vo.Address;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreSearchResponse;
import org.dinosaur.foodbowl.global.util.PointUtils;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class StoreCustomRepositoryTest extends PersistenceTest {

    @Autowired
    private StoreCustomRepository storeCustomRepository;

    @Nested
    class 팔로잉_하는_유저의_리뷰가_작성된_가게_목록_범위_조회_시 {

        @Test
        void 팔로잉_하는_유저의_리뷰가_작성된_가게는_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().member(writer).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result =
                    storeCustomRepository.findStoresByFollowingInMapBounds(member.getId(), mapCoordinateBoundDto);

            assertThat(result).containsExactly(store);
        }

        @Test
        void 팔로잉_하는_유저의_리뷰가_작성되지_않은_가게는_조회하지_않는다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result =
                    storeCustomRepository.findStoresByFollowingInMapBounds(member.getId(), mapCoordinateBoundDto);

            assertThat(result).isEmpty();
        }

        @Test
        void 팔로잉_하는_유저가_작성한_리뷰가_동일한_가게인_경우_중복_조회_하지_않는다() {
            Member member = memberTestPersister.builder().save();
            Member writerA = memberTestPersister.builder().save();
            Member writerB = memberTestPersister.builder().save();
            followTestPersister.builder().following(writerA).follower(member).save();
            followTestPersister.builder().following(writerB).follower(member).save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().member(writerA).store(store).save();
            reviewTestPersister.builder().member(writerB).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result =
                    storeCustomRepository.findStoresByFollowingInMapBounds(member.getId(), mapCoordinateBoundDto);

            assertThat(result).containsExactly(store);
        }

        @Test
        void 폴리곤_영역에_경도와_위도가_속하지_않는_가게의_리뷰는_조회하지_않는다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().member(writer).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX() + 10),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY() + 10),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result =
                    storeCustomRepository.findStoresByFollowingInMapBounds(member.getId(), mapCoordinateBoundDto);

            assertThat(result).isEmpty();
        }
    }

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
