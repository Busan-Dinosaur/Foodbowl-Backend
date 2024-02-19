package org.dinosaur.foodbowl.domain.store.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.dinosaur.foodbowl.domain.store.domain.vo.CategoryType.카페;

import java.math.BigDecimal;
import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.application.dto.MapCoordinateBoundDto;
import org.dinosaur.foodbowl.domain.store.domain.Category;
import org.dinosaur.foodbowl.domain.store.domain.School;
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

    @Autowired
    private CategoryRepository categoryRepository;

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
                .storeName("김밥세상")
                .save();

        List<StoreSearchResponse> responses = storeCustomRepository.search(name, x, y, 10);

        List<Long> responseStoreIds = responses.stream()
                .map(StoreSearchResponse::storeId)
                .toList();
        assertSoftly(softly -> {
            assertThat(responseStoreIds).containsExactly(storeB.getId(), storeD.getId(), storeA.getId());
            assertThat(responseStoreIds).hasSize(3);
            assertThat(responseStoreIds).doesNotContain(storeC.getId());
            assertThat(responses.get(0).category()).isEqualTo(카페.name());
            assertThat(responses.get(0).address()).isEqualTo("서울시 서초구 방배동 1234");
        });
    }

    private Address createAddress(double x, double y) {
        return Address.of(
                "서울시 서초구 방배동 1234",
                PointUtils.generate(new BigDecimal(x), new BigDecimal(y))
        );
    }

    @Nested
    class 위도_경도와_폴리곤_영역에_해당하는_가게_목록_범위_조회_시 {

        @Test
        void 영역에_포함된_가게는_조회한다() {
            Member member = memberTestPersister.builder().save();
            Store storeA = storeTestPersister.builder().save();
            Store storeB = storeTestPersister.builder().address(storeA.getAddress()).save();
            reviewTestPersister.builder().member(member).store(storeA).save();
            reviewTestPersister.builder().member(member).store(storeB).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(storeA.getAddress().getCoordinate().getX() + 0.5),
                    BigDecimal.valueOf(storeA.getAddress().getCoordinate().getY() + 0.5),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result =
                    storeCustomRepository.findStoresByInMapBounds(mapCoordinateBoundDto, null);

            assertThat(result).containsExactly(storeA, storeB);
        }

        @Test
        void 영역에_포함되고_카테고리가_일치하는_가게는_조회한다() {
            Category category = categoryRepository.findById(3L);
            Member member = memberTestPersister.builder().save();
            Store storeA = storeTestPersister.builder().category(category).save();
            Store storeB = storeTestPersister.builder().address(storeA.getAddress()).save();
            reviewTestPersister.builder().member(member).store(storeA).save();
            reviewTestPersister.builder().member(member).store(storeB).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(storeA.getAddress().getCoordinate().getX() + 0.5),
                    BigDecimal.valueOf(storeA.getAddress().getCoordinate().getY() + 0.5),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result = storeCustomRepository.findStoresByInMapBounds(
                    mapCoordinateBoundDto,
                    storeA.getCategory().getCategoryType()
            );

            assertSoftly(softly -> {
                softly.assertThat(result).containsExactly(storeA);
                softly.assertThat(result).doesNotContain(storeB);
            });
        }

        @Test
        void 영역에_포함되지_않는_가게는_조회되지_않는다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().member(member).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX() + 10),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY() + 10),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result =
                    storeCustomRepository.findStoresByInMapBounds(mapCoordinateBoundDto, null);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class 멤버의_리뷰가_작성된_가게_목록_범위_조회_시 {

        @Test
        void 멤버의_리뷰가_작성된_가게는_조회한다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().member(member).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result =
                    storeCustomRepository.findStoresByMemberInMapBounds(member.getId(), mapCoordinateBoundDto);

            assertThat(result).contains(store);
        }

        @Test
        void 멤버의_리뷰가_작성되지_않은_가게는_조회하지_않는다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result =
                    storeCustomRepository.findStoresByMemberInMapBounds(member.getId(), mapCoordinateBoundDto);

            assertThat(result).isEmpty();
        }

        @Test
        void 폴리곤_영역에_경도와_위도가_속하지_않는_가게의_리뷰는_조회하지_않는다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().member(member).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX() + 10),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY() + 10),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result =
                    storeCustomRepository.findStoresByMemberInMapBounds(member.getId(), mapCoordinateBoundDto);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class 북마크한_가게_목록_범위_조회_시 {

        @Test
        void 북마크한_가게는_조회한다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            bookmarkTestPersister.builder().member(member).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result =
                    storeCustomRepository.findStoresByBookmarkInMapBounds(member.getId(), mapCoordinateBoundDto);

            assertThat(result).containsExactly(store);
        }

        @Test
        void 북마크하지_않은_가게는_조회하지_않는다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result =
                    storeCustomRepository.findStoresByBookmarkInMapBounds(member.getId(), mapCoordinateBoundDto);

            assertThat(result).isEmpty();
        }

        @Test
        void 폴리곤_영역에_경도와_위도가_속하지_않는_가게의_리뷰는_조회하지_않는다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            bookmarkTestPersister.builder().member(member).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX() + 10),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY() + 10),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result =
                    storeCustomRepository.findStoresByBookmarkInMapBounds(member.getId(), mapCoordinateBoundDto);

            assertThat(result).isEmpty();
        }
    }

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
        void 팔로잉_유저의_리뷰가_작성된_가게를_조회할_때_사용자가_작성한_리뷰의_가게도_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store storeA = storeTestPersister.builder().save();
            Store storeB = storeTestPersister.builder().address(storeA.getAddress()).save();
            reviewTestPersister.builder().member(writer).store(storeA).save();
            reviewTestPersister.builder().member(member).store(storeB).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(storeA.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(storeA.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result =
                    storeCustomRepository.findStoresByFollowingInMapBounds(member.getId(), mapCoordinateBoundDto);

            assertThat(result).containsExactly(storeA, storeB);
        }

        @Test
        void 팔로잉_유저의_리뷰가_존재하지_않아도_사용자가_작성한_리뷰의_가게는_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().member(member).store(store).save();
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
        void 팔로잉_유저가_존재하지_않아도_사용자가_작성한_리뷰의_가게는_조회한다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().member(member).store(store).save();
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

    @Nested
    class 학교_근처_가게_목록_범위_조회_시 {

        @Test
        void 학교_근처_가게는_조회한다() {
            Store store = storeTestPersister.builder().save();
            School school = schoolTestPersister.builder().save();
            storeSchoolTestPersister.builder().store(store).school(school).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result =
                    storeCustomRepository.findStoresBySchoolInMapBounds(school.getId(), mapCoordinateBoundDto);

            assertThat(result).contains(store);
        }

        @Test
        void 학교_근처가_아닌_가게는_조회하지_않는다() {
            Store store = storeTestPersister.builder().save();
            School school = schoolTestPersister.builder().save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result =
                    storeCustomRepository.findStoresBySchoolInMapBounds(school.getId(), mapCoordinateBoundDto);

            assertThat(result).isEmpty();
        }

        @Test
        void 폴리곤_영역에_경도와_위도가_속하지_않는_가게의_리뷰는_조회하지_않는다() {
            Store store = storeTestPersister.builder().save();
            School school = schoolTestPersister.builder().save();
            storeSchoolTestPersister.builder().store(store).school(school).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX() + 10),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY() + 10),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Store> result =
                    storeCustomRepository.findStoresBySchoolInMapBounds(school.getId(), mapCoordinateBoundDto);

            assertThat(result).isEmpty();
        }
    }
}
