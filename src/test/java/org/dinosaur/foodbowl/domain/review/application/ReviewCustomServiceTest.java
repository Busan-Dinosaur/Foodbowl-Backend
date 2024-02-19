package org.dinosaur.foodbowl.domain.review.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.application.dto.MapCoordinateBoundDto;
import org.dinosaur.foodbowl.domain.review.application.dto.StoreToReviewCountDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.domain.vo.ReviewFilter;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.vo.CategoryType;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ReviewCustomServiceTest extends IntegrationTest {

    @Autowired
    private ReviewCustomService reviewCustomService;

    @Test
    void 가게_목록에_속한_가게의_리뷰_개수를_조회한다() {
        Member writer = memberTestPersister.builder().save();
        Store storeA = storeTestPersister.builder().save();
        Store storeB = storeTestPersister.builder().save();
        Store storeC = storeTestPersister.builder().save();
        reviewTestPersister.builder().member(writer).store(storeA).save();
        reviewTestPersister.builder().member(writer).store(storeA).save();
        reviewTestPersister.builder().member(writer).store(storeB).save();

        StoreToReviewCountDto result = reviewCustomService.getReviewCountByStores(List.of(storeA, storeC));

        assertSoftly(softly -> {
            softly.assertThat(result.getReviewCount(storeA.getId())).isEqualTo(2);
            softly.assertThat(result.getReviewCount(storeB.getId())).isEqualTo(0);
            softly.assertThat(result.getReviewCount(storeC.getId())).isEqualTo(0);
        });
    }

    @Test
    void 모든_리뷰_목록을_범위를_통해_조회한다() {
        Member writerA = memberTestPersister.builder().save();
        Member writerB = memberTestPersister.builder().save();
        Store store = storeTestPersister.builder().save();
        Review reviewA = reviewTestPersister.builder().member(writerA).store(store).save();
        Review reviewB = reviewTestPersister.builder().member(writerB).store(store).save();
        MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                BigDecimal.valueOf(3),
                BigDecimal.valueOf(3)
        );

        List<Review> result = reviewCustomService.getReviewsInMapBounds(
                null,
                mapCoordinateBoundDto,
                null,
                10
        );

        assertThat(result).containsExactly(reviewB, reviewA);
    }

    @Test
    void 멤버_리뷰_목록을_범위를_통해_조회한다() {
        Member writer = memberTestPersister.builder().save();
        Store store = storeTestPersister.builder().save();
        Review review = reviewTestPersister.builder().member(writer).store(store).save();
        MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                BigDecimal.valueOf(3),
                BigDecimal.valueOf(3)
        );

        List<Review> result = reviewCustomService.getReviewsByMemberInMapBounds(
                writer.getId(),
                null,
                mapCoordinateBoundDto,
                null,
                10
        );

        assertThat(result).containsExactly(review);
    }

    @Test
    void 멤버_리뷰_목록을_카테고리_필터링과_범위를_통해_조회한다() {
        Member writer = memberTestPersister.builder().save();
        Store store = storeTestPersister.builder().save();
        Review review = reviewTestPersister.builder().member(writer).store(store).save();
        MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                BigDecimal.valueOf(3),
                BigDecimal.valueOf(3)
        );

        List<Review> result = reviewCustomService.getReviewsByMemberInMapBounds(
                writer.getId(),
                null,
                mapCoordinateBoundDto,
                CategoryType.카페,
                10
        );

        assertThat(result).containsExactly(review);
    }

    @Test
    void 가게에_해당하는_리뷰를_조회한다() {
        Store store = storeTestPersister.builder().save();
        Review review = reviewTestPersister.builder().store(store).save();

        List<Review> reviews = reviewCustomService.getReviewsByStore(
                store.getId(),
                ReviewFilter.ALL,
                1L,
                null,
                10
        );

        assertThat(reviews).containsExactly(review);
    }

    @Test
    void 북마크한_가게_리뷰_목록을_범위를_통해_조회한다() {
        Member member = memberTestPersister.builder().save();
        Store store = storeTestPersister.builder().save();
        Review review = reviewTestPersister.builder().store(store).save();
        bookmarkTestPersister.builder().member(member).store(store).save();
        MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                BigDecimal.valueOf(3),
                BigDecimal.valueOf(3)
        );

        List<Review> result = reviewCustomService.getReviewsByBookmarkInMapBounds(
                member.getId(),
                null,
                mapCoordinateBoundDto,
                10
        );

        assertThat(result).containsExactly(review);
    }

    @Test
    void 팔로잉_하는_멤버의_리뷰_목록을_범위를_통해_조회한다() {
        Member member = memberTestPersister.builder().save();
        Member writer = memberTestPersister.builder().save();
        followTestPersister.builder().following(writer).follower(member).save();
        Store store = storeTestPersister.builder().save();
        Review review = reviewTestPersister.builder().member(writer).store(store).save();
        MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                BigDecimal.valueOf(1),
                BigDecimal.valueOf(1)
        );

        List<Review> result = reviewCustomService.getReviewsByFollowingInMapBounds(
                member.getId(),
                null,
                mapCoordinateBoundDto,
                null,
                10
        );

        assertThat(result).containsExactly(review);
    }

    @Test
    void 팔로잉_하는_멤버의_리뷰_목록을_카테고리와_범위를_통해_조회한다() {
        Member member = memberTestPersister.builder().save();
        Member writer = memberTestPersister.builder().save();
        followTestPersister.builder().following(writer).follower(member).save();
        Store store = storeTestPersister.builder().save();
        Review review = reviewTestPersister.builder().member(writer).store(store).save();
        MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                BigDecimal.valueOf(1),
                BigDecimal.valueOf(1)
        );

        List<Review> result = reviewCustomService.getReviewsByFollowingInMapBounds(
                member.getId(),
                null,
                mapCoordinateBoundDto,
                CategoryType.카페,
                10
        );

        assertThat(result).containsExactly(review);
    }

    @Test
    void 학교_근처_리뷰_목록을_범위를_통해_조회한다() {
        Store store = storeTestPersister.builder().save();
        School school = schoolTestPersister.builder().save();
        storeSchoolTestPersister.builder().store(store).school(school).save();
        Review review = reviewTestPersister.builder().store(store).save();
        MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                BigDecimal.valueOf(3),
                BigDecimal.valueOf(3)
        );

        List<Review> result = reviewCustomService.getReviewsBySchoolInMapBounds(
                school.getId(),
                null,
                mapCoordinateBoundDto,
                null,
                10
        );

        assertThat(result).containsExactly(review);
    }

    @Test
    void 학교_근처_리뷰_목록을_카테고리_필터링과_범위를_통해_조회한다() {
        Store store = storeTestPersister.builder().save();
        School school = schoolTestPersister.builder().save();
        storeSchoolTestPersister.builder().store(store).school(school).save();
        Review review = reviewTestPersister.builder().store(store).save();
        MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                BigDecimal.valueOf(3),
                BigDecimal.valueOf(3)
        );

        List<Review> result = reviewCustomService.getReviewsBySchoolInMapBounds(
                school.getId(),
                null,
                mapCoordinateBoundDto,
                CategoryType.카페,
                10
        );

        assertThat(result).containsExactly(review);
    }

    @Test
    void 사진이_있는_리뷰_피드를_조회한다() {
        Store store = storeTestPersister.builder().save();
        Photo photo = photoTestPersister.builder().save();
        Review review = reviewTestPersister.builder().store(store).save();
        reviewPhotoTestPersister.builder().review(review).photo(photo).save();

        List<Review> reviews = reviewCustomService.getReviewFeeds(null, 10);

        assertThat(reviews).containsExactly(review);
    }
}
