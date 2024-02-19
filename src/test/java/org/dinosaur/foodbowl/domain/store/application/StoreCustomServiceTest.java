package org.dinosaur.foodbowl.domain.store.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.application.dto.MapCoordinateBoundDto;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class StoreCustomServiceTest extends IntegrationTest {

    @Autowired
    private StoreCustomService storeCustomService;

    @Test
    void 위도_경도와_폴리곤_영역에_해당하는_가게_목록을_조회한다() {
        Store store = storeTestPersister.builder().save();
        reviewTestPersister.builder().store(store).save();
        MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                BigDecimal.valueOf(1),
                BigDecimal.valueOf(1)
        );

        List<Store> stores = storeCustomService.getStoresInMapBounds(mapCoordinateBoundDto);

        assertThat(stores).containsExactly(store);
    }

    @Test
    void 멤버가_작성한_리뷰가_존재하는_가게_목록을_범위를_통해_조회한다() {
        Member member = memberTestPersister.builder().save();
        Store store = storeTestPersister.builder().save();
        reviewTestPersister.builder().member(member).store(store).save();
        MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                BigDecimal.valueOf(1),
                BigDecimal.valueOf(1)
        );

        List<Store> result = storeCustomService.getStoresByMemberInMapBounds(member.getId(), mapCoordinateBoundDto);

        assertThat(result).contains(store);
    }

    @Test
    void 북마크한_가게_목록을_범위를_통해_조회한다() {
        Member member = memberTestPersister.builder().save();
        Store store = storeTestPersister.builder().save();
        bookmarkTestPersister.builder().member(member).store(store).save();
        MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                BigDecimal.valueOf(1),
                BigDecimal.valueOf(1)
        );

        List<Store> result = storeCustomService.getStoresByBookmarkInMapBounds(member.getId(), mapCoordinateBoundDto);

        assertThat(result).containsExactly(store);
    }

    @Test
    void 팔로잉_하는_유저의_리뷰가_작성된_가게_목록을_범위를_통해_조회한다() {
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

        List<Store> result = storeCustomService.getStoresByFollowingInMapBounds(member.getId(), mapCoordinateBoundDto);

        assertThat(result).containsExactly(store);
    }

    @Test
    void 학교_근거의_가게_목록을_범위를_통해_조회한다() {
        Store store = storeTestPersister.builder().save();
        School school = schoolTestPersister.builder().save();
        storeSchoolTestPersister.builder().store(store).school(school).save();
        MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                BigDecimal.valueOf(1),
                BigDecimal.valueOf(1)
        );

        List<Store> result = storeCustomService.getStoresBySchoolInMapBounds(school.getId(), mapCoordinateBoundDto);

        assertThat(result).contains(store);
    }
}
