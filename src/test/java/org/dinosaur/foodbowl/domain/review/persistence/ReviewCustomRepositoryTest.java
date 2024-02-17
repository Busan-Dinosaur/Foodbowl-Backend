package org.dinosaur.foodbowl.domain.review.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.application.dto.MapCoordinateBoundDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.domain.vo.ReviewFilter;
import org.dinosaur.foodbowl.domain.review.persistence.dto.StoreReviewCountDto;
import org.dinosaur.foodbowl.domain.store.domain.Category;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.persistence.CategoryRepository;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ReviewCustomRepositoryTest extends PersistenceTest {

    @Autowired
    private ReviewCustomRepository reviewCustomRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void 가게_목록에_존재하는_가게의_리뷰_개수를_조회한다() {
        Member writer = memberTestPersister.builder().save();
        Store storeA = storeTestPersister.builder().save();
        Store storeB = storeTestPersister.builder().save();
        Store storeC = storeTestPersister.builder().save();
        reviewTestPersister.builder().member(writer).store(storeA).save();
        reviewTestPersister.builder().member(writer).store(storeA).save();
        reviewTestPersister.builder().member(writer).store(storeB).save();

        List<StoreReviewCountDto> result =
                reviewCustomRepository.findReviewCountByStores(List.of(storeA, storeB, storeC));

        assertSoftly(softly -> {
            softly.assertThat(result).hasSize(2);
            softly.assertThat(result.get(0).storeId()).isEqualTo(storeA.getId());
            softly.assertThat(result.get(0).reviewCount()).isEqualTo(2);
            softly.assertThat(result.get(1).storeId()).isEqualTo(storeB.getId());
            softly.assertThat(result.get(1).reviewCount()).isEqualTo(1);
        });
    }

    @Nested
    class 사진이_포함된_리뷰_조회_시 {

        @Test
        void 최신_순서로_페이징_조회한다() {
            Member writer = memberTestPersister.builder().save();
            Store storeA = storeTestPersister.builder().save();
            Store storeB = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().member(writer).store(storeA).save();
            Review reviewB = reviewTestPersister.builder().member(writer).store(storeA).save();
            Review reviewC = reviewTestPersister.builder().member(writer).store(storeB).save();
            Photo photoA = photoTestPersister.builder().save();
            Photo photoB = photoTestPersister.builder().save();
            Photo photoC = photoTestPersister.builder().save();
            reviewPhotoTestPersister.builder().review(reviewB).photo(photoA).save();
            reviewPhotoTestPersister.builder().review(reviewC).photo(photoB).save();
            reviewPhotoTestPersister.builder().review(reviewC).photo(photoC).save();

            List<Review> reviews = reviewCustomRepository.findPaginationReviewsHavingPhoto(null, 10);

            assertThat(reviews).containsExactly(reviewC, reviewB);
        }

        @Test
        void 사진이_없는_경우_조회하지_않는다() {
            Member writer = memberTestPersister.builder().save();
            Store storeA = storeTestPersister.builder().save();
            Store storeB = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().member(writer).store(storeA).save();
            Review reviewB = reviewTestPersister.builder().member(writer).store(storeA).save();
            Review reviewC = reviewTestPersister.builder().member(writer).store(storeB).save();

            List<Review> reviews = reviewCustomRepository.findPaginationReviewsHavingPhoto(null, 10);

            assertThat(reviews).isEmpty();
        }
    }

    @Nested
    class 멤버의_리뷰_목록_페이징_조회_시 {

        @Test
        void 마지막_리뷰ID가_NULL이_아닐때_마지막_리뷰ID보다_작은_리뷰는_조회한다() {
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().member(writer).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByMemberInMapBound(
                    writer.getId(),
                    review.getId() + 1,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).containsExactly(review);
        }

        @Test
        void 마지막_리뷰ID가_NULL이_아닐때_마지막_리뷰ID보다_큰_리뷰는_조회하지_않는다() {
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().member(writer).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByMemberInMapBound(
                    writer.getId(),
                    review.getId() - 1,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 멤버의_리뷰라면_조회한다() {
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().member(writer).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByMemberInMapBound(
                    writer.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).containsExactly(review);
        }

        @Test
        void 멤버의_리뷰가_아니라면_조회하지_않는다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().member(writer).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByMemberInMapBound(
                    member.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 폴리곤_영역에_경도와_위도가_속하지_않는_가게의_리뷰는_조회하지_않는다() {
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().member(writer).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX() + 10),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY() + 10),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByMemberInMapBound(
                    writer.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 리뷰ID를_내림차순으로_조회한다() {
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().member(writer).store(store).save();
            Review reviewB = reviewTestPersister.builder().member(writer).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByMemberInMapBound(
                    writer.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).containsExactly(reviewB, reviewA);
        }

        @Test
        void 페이지_크기만큼_조회한다() {
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().member(writer).store(store).save();
            Review reviewB = reviewTestPersister.builder().member(writer).store(store).save();
            Review reviewC = reviewTestPersister.builder().member(writer).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByMemberInMapBound(
                    writer.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    2
            );

            assertThat(result).containsExactly(reviewC, reviewB);
        }

        @Test
        void 카테고리_필터링을_적용해_조회한다() {
            Category category = categoryRepository.findById(2L);
            Member writer = memberTestPersister.builder().save();
            Store storeA = storeTestPersister.builder().category(category).save();
            Store storeB = storeTestPersister.builder().address(storeA.getAddress()).save();
            Review reviewA = reviewTestPersister.builder().member(writer).store(storeA).save();
            Review reviewB = reviewTestPersister.builder().member(writer).store(storeA).save();
            Review reviewC = reviewTestPersister.builder().member(writer).store(storeB).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(storeA.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(storeA.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByMemberInMapBound(
                    writer.getId(),
                    null,
                    mapCoordinateBoundDto,
                    category.getCategoryType(),
                    10
            );

            assertSoftly(softly -> {
                softly.assertThat(result).containsExactly(reviewB, reviewA);
                softly.assertThat(result).doesNotContain(reviewC);
            });
        }
    }

    @Nested
    class 가게_리뷰_목록_페이징_조회_시 {

        @Test
        void 정상적으로_조회한다() {
            Member loginMember = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().store(store).save();
            Review reviewB = reviewTestPersister.builder().store(store).save();
            Review reviewC = reviewTestPersister.builder().store(store).save();

            List<Review> reviews = reviewCustomRepository.findPaginationReviewsByStore(
                    store.getId(),
                    ReviewFilter.ALL,
                    loginMember.getId(),
                    null,
                    10
            );

            assertThat(reviews).containsExactly(reviewC, reviewB, reviewA);
        }

        @Test
        void 친구_필터링이_있으면_사용자_및_팔로워_리뷰만_조회한다() {
            Member loginMember = memberTestPersister.builder().save();
            Member gray = memberTestPersister.builder().save();
            Member dazzle = memberTestPersister.builder().save();
            followTestPersister.builder().follower(loginMember).following(gray).save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().member(gray).store(store).save();
            Review reviewB = reviewTestPersister.builder().member(gray).store(store).save();
            Review reviewC = reviewTestPersister.builder().member(dazzle).store(store).save();
            Review reviewD = reviewTestPersister.builder().member(loginMember).store(store).save();

            List<Review> reviews = reviewCustomRepository.findPaginationReviewsByStore(
                    store.getId(),
                    ReviewFilter.FRIEND,
                    loginMember.getId(),
                    null,
                    10
            );

            assertSoftly(softly -> {
                softly.assertThat(reviews).containsExactly(reviewD, reviewB, reviewA);
                softly.assertThat(reviews).doesNotContain(reviewC);
            });
        }

        @Test
        void 친구_필터링이_있을_때_팔로워가_없어도_사용자_리뷰는_조회된다() {
            Member loginMember = memberTestPersister.builder().save();
            Member dazzle = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().member(dazzle).store(store).save();
            Review reviewB = reviewTestPersister.builder().member(loginMember).store(store).save();
            Review reviewC = reviewTestPersister.builder().member(loginMember).store(store).save();

            List<Review> reviews = reviewCustomRepository.findPaginationReviewsByStore(
                    store.getId(),
                    ReviewFilter.FRIEND,
                    loginMember.getId(),
                    null,
                    10
            );

            assertSoftly(softly -> {
                softly.assertThat(reviews).containsExactly(reviewC, reviewB);
                softly.assertThat(reviews).doesNotContain(reviewA);
            });
        }

        @Test
        void 친구_필터링이_있을_때_팔로워가_있지만_팔로워의_리뷰가_없어도_사용자_리뷰는_조회된다() {
            Member loginMember = memberTestPersister.builder().save();
            Member dazzle = memberTestPersister.builder().save();
            followTestPersister.builder().follower(loginMember).following(dazzle).save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().member(loginMember).store(store).save();
            Review reviewB = reviewTestPersister.builder().member(loginMember).store(store).save();

            List<Review> reviews = reviewCustomRepository.findPaginationReviewsByStore(
                    store.getId(),
                    ReviewFilter.FRIEND,
                    loginMember.getId(),
                    null,
                    10
            );


            assertThat(reviews).containsExactly(reviewB, reviewA);
        }

        @Test
        void 전체_필터링이_있으면_모든_리뷰를_조회한다() {
            Member loginMember = memberTestPersister.builder().save();
            Member gray = memberTestPersister.builder().save();
            Member dazzle = memberTestPersister.builder().save();
            followTestPersister.builder().follower(loginMember).following(gray).save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().member(gray).store(store).save();
            Review reviewB = reviewTestPersister.builder().member(gray).store(store).save();
            Review reviewC = reviewTestPersister.builder().member(dazzle).store(store).save();

            List<Review> reviews = reviewCustomRepository.findPaginationReviewsByStore(
                    store.getId(),
                    ReviewFilter.ALL,
                    loginMember.getId(),
                    null,
                    10
            );

            assertThat(reviews).containsExactly(reviewC, reviewB, reviewA);
        }

        @Test
        void 마지막_리뷰ID가_NULL이_아닐때_마지막_리뷰ID보다_작은_리뷰는_조회한다() {
            Member loginMember = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().store(store).save();
            Review reviewB = reviewTestPersister.builder().store(store).save();

            List<Review> reviews = reviewCustomRepository.findPaginationReviewsByStore(
                    store.getId(),
                    ReviewFilter.ALL,
                    loginMember.getId(),
                    reviewB.getId() + 1,
                    10
            );

            assertThat(reviews).containsExactly(reviewB, reviewA);
        }

        @Test
        void 마지막_리뷰ID가_NULL이_아닐때_마지막_리뷰ID보다_큰_리뷰는_조회하지_않는다() {
            Member loginMember = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().store(store).save();
            Review reviewB = reviewTestPersister.builder().store(store).save();

            List<Review> reviews = reviewCustomRepository.findPaginationReviewsByStore(
                    store.getId(),
                    ReviewFilter.ALL,
                    loginMember.getId(),
                    reviewA.getId() + 1,
                    10
            );

            assertSoftly(softly -> {
                softly.assertThat(reviews).containsExactly(reviewA);
                softly.assertThat(reviews).doesNotContain(reviewB);
            });
        }

        @Test
        void 리뷰ID를_내림차순으로_조회한다() {
            Member loginMember = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().store(store).save();
            Review reviewB = reviewTestPersister.builder().store(store).save();

            List<Review> result = reviewCustomRepository.findPaginationReviewsByStore(
                    store.getId(),
                    ReviewFilter.ALL,
                    loginMember.getId(),
                    null,
                    10
            );

            assertThat(result).containsExactly(reviewB, reviewA);
        }

        @Test
        void 페이지_크기만큼_조회한다() {
            Member loginMember = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().store(store).save();
            Review reviewB = reviewTestPersister.builder().store(store).save();
            Review reviewC = reviewTestPersister.builder().store(store).save();

            List<Review> result = reviewCustomRepository.findPaginationReviewsByStore(
                    store.getId(),
                    ReviewFilter.ALL,
                    loginMember.getId(),
                    null,
                    2
            );

            assertThat(result).containsExactly(reviewC, reviewB);
        }
    }

    @Nested
    class 북마크한_가게_리뷰_목록_페이징_조회_시 {

        @Test
        void 마지막_리뷰ID가_NULL이_아닐때_마지막_리뷰ID보다_작은_리뷰는_조회한다() {
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

            List<Review> result = reviewCustomRepository.findPaginationReviewsByBookmarkInMapBounds(
                    member.getId(),
                    review.getId() + 1,
                    mapCoordinateBoundDto,
                    10
            );

            assertThat(result).containsExactly(review);
        }

        @Test
        void 마지막_리뷰ID가_NULL이_아닐때_마지막_리뷰ID보다_큰_리뷰는_조회하지_않는다() {
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

            List<Review> result = reviewCustomRepository.findPaginationReviewsByBookmarkInMapBounds(
                    member.getId(),
                    review.getId() - 1,
                    mapCoordinateBoundDto,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 북마크한_가게_리뷰를_조회한다() {
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

            List<Review> result = reviewCustomRepository.findPaginationReviewsByBookmarkInMapBounds(
                    member.getId(),
                    null,
                    mapCoordinateBoundDto,
                    10
            );

            assertThat(result).containsExactly(review);
        }

        @Test
        void 북마크_하지_않은_가게_리뷰는_조회하지_않는다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByBookmarkInMapBounds(
                    member.getId(),
                    null,
                    mapCoordinateBoundDto,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 폴리곤_영역에_경도와_위도가_속하지_않는_가게의_리뷰는_조회하지_않는다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().store(store).save();
            bookmarkTestPersister.builder().member(member).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX() + 10),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY() + 10),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByBookmarkInMapBounds(
                    member.getId(),
                    null,
                    mapCoordinateBoundDto,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 리뷰ID를_내림차순으로_조회한다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().store(store).save();
            Review reviewB = reviewTestPersister.builder().store(store).save();
            bookmarkTestPersister.builder().member(member).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByBookmarkInMapBounds(
                    member.getId(),
                    null,
                    mapCoordinateBoundDto,
                    10
            );

            assertThat(result).containsExactly(reviewB, reviewA);
        }

        @Test
        void 페이지_크기만큼_조회한다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().store(store).save();
            Review reviewB = reviewTestPersister.builder().store(store).save();
            Review reviewC = reviewTestPersister.builder().store(store).save();
            bookmarkTestPersister.builder().member(member).store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByBookmarkInMapBounds(
                    member.getId(),
                    null,
                    mapCoordinateBoundDto,
                    2
            );

            assertThat(result).containsExactly(reviewC, reviewB);
        }
    }

    @Nested
    class 팔로잉_유저의_리뷰_목록_페이징_조회_시 {

        @Test
        void 마지막_리뷰ID가_NULL이_아닐때_마지막_리뷰ID보다_작은_리뷰는_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().store(store).member(writer).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByFollowingInMapBounds(
                    member.getId(),
                    review.getId() + 1,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).containsExactly(review);
        }

        @Test
        void 마지막_리뷰ID가_NULL이_아닐때_마지막_리뷰ID보다_큰_리뷰는_조회하지_않는다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().store(store).member(writer).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByFollowingInMapBounds(
                    member.getId(),
                    review.getId() - 1,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 팔로잉_하고있는_유저의_리뷰를_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().store(store).member(writer).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByFollowingInMapBounds(
                    member.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).containsExactly(review);
        }

        @Test
        void 사용자의_리뷰도_함께_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().store(store).member(writer).save();
            Review reviewA = reviewTestPersister.builder().store(store).member(member).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByFollowingInMapBounds(
                    member.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).containsExactly(reviewA, review);
        }

        @Test
        void 사용자의_팔로잉이_없는_경우에도_사용자의_리뷰는_조회된다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().store(store).member(member).save();
            Review reviewB = reviewTestPersister.builder().store(store).member(member).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByFollowingInMapBounds(
                    member.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).containsExactly(reviewB, reviewA);
        }

        @Test
        void 사용자가_아닌_팔로잉_하고있지_않은_유저의_리뷰는_조회하지_않는다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().store(store).member(writer).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByFollowingInMapBounds(
                    member.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 폴리곤_영역에_경도와_위도가_속하지_않는_가게의_리뷰는_조회하지_않는다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().store(store).member(writer).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX() + 10),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY() + 10),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByFollowingInMapBounds(
                    member.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 리뷰ID를_내림차순으로_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().store(store).member(writer).save();
            Review reviewB = reviewTestPersister.builder().store(store).member(writer).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByFollowingInMapBounds(
                    member.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).containsExactly(reviewB, reviewA);
        }

        @Test
        void 카테고리_조건이_없으면_모든_결과를_페이지_크기만큼_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().store(store).member(writer).save();
            Review reviewB = reviewTestPersister.builder().store(store).member(writer).save();
            Review reviewC = reviewTestPersister.builder().store(store).member(writer).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByFollowingInMapBounds(
                    member.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    2
            );

            assertThat(result).containsExactly(reviewC, reviewB);
        }

        @Test
        void 카테고리_필터링_조건이_있으면_필터링된_결과를_조회한다() {
            Category category = categoryRepository.findById(3L);
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store storeA = storeTestPersister.builder().category(category).save();
            Store storeB = storeTestPersister.builder().address(storeA.getAddress()).save();
            Review reviewA = reviewTestPersister.builder().store(storeB).member(writer).save();
            Review reviewB = reviewTestPersister.builder().store(storeA).member(writer).save();
            Review reviewC = reviewTestPersister.builder().store(storeA).member(writer).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(storeA.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(storeA.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsByFollowingInMapBounds(
                    member.getId(),
                    null,
                    mapCoordinateBoundDto,
                    category.getCategoryType(),
                    10
            );

            assertSoftly(softly -> {
                softly.assertThat(result).containsExactly(reviewC, reviewB);
                softly.assertThat(result).doesNotContain(reviewA);
            });
        }
    }

    @Nested
    class 학교_근처_리뷰_목록_페이징_조회_시 {

        @Test
        void 마지막_리뷰ID가_NULL이_아닐때_마지막_리뷰ID보다_작은_리뷰는_조회한다() {
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

            List<Review> result = reviewCustomRepository.findPaginationReviewsBySchoolInMapBounds(
                    school.getId(),
                    review.getId() + 1,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).containsExactly(review);
        }

        @Test
        void 마지막_리뷰ID가_NULL이_아닐때_마지막_리뷰ID보다_큰_리뷰는_조회하지_않는다() {
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

            List<Review> result = reviewCustomRepository.findPaginationReviewsBySchoolInMapBounds(
                    school.getId(),
                    review.getId() - 1,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 학교가_일치하는_가게의_리뷰는_조회한다() {
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

            List<Review> result = reviewCustomRepository.findPaginationReviewsBySchoolInMapBounds(
                    school.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).containsExactly(review);
        }

        @Test
        void 학교가_일치하지_않는_가게의_리뷰는_조회하지_않는다() {
            Store store = storeTestPersister.builder().save();
            School schoolA = schoolTestPersister.builder().save();
            School schoolB = schoolTestPersister.builder().save();
            storeSchoolTestPersister.builder().store(store).school(schoolA).save();
            Review review = reviewTestPersister.builder().store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsBySchoolInMapBounds(
                    schoolB.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 폴리곤_영역에_경도와_위도가_속하지_않는_가게의_리뷰는_조회하지_않는다() {
            Store store = storeTestPersister.builder().save();
            School school = schoolTestPersister.builder().save();
            storeSchoolTestPersister.builder().store(store).school(school).save();
            Review review = reviewTestPersister.builder().store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX() + 10),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY() + 10),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsBySchoolInMapBounds(
                    school.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 리뷰ID를_내림차순으로_조회한다() {
            Store store = storeTestPersister.builder().save();
            School school = schoolTestPersister.builder().save();
            storeSchoolTestPersister.builder().store(store).school(school).save();
            Review reviewA = reviewTestPersister.builder().store(store).save();
            Review reviewB = reviewTestPersister.builder().store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsBySchoolInMapBounds(
                    school.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    10
            );

            assertThat(result).containsExactly(reviewB, reviewA);
        }

        @Test
        void 카테고리_필터링_조건이_없으면_모든_리뷰를_페이지_크기만큼_조회한다() {
            Store store = storeTestPersister.builder().save();
            School school = schoolTestPersister.builder().save();
            storeSchoolTestPersister.builder().store(store).school(school).save();
            Review reviewA = reviewTestPersister.builder().store(store).save();
            Review reviewB = reviewTestPersister.builder().store(store).save();
            Review reviewC = reviewTestPersister.builder().store(store).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsBySchoolInMapBounds(
                    school.getId(),
                    null,
                    mapCoordinateBoundDto,
                    null,
                    2
            );

            assertThat(result).containsExactly(reviewC, reviewB);
        }

        @Test
        void 카테고리_필터링_조건이_있으면_필터링된_결과를_조회한다() {
            Category categoryA = categoryRepository.findById(1L);
            Category categoryB = categoryRepository.findById(2L);
            Store storeA = storeTestPersister.builder().category(categoryA).save();
            Store storeB = storeTestPersister.builder().category(categoryB).address(storeA.getAddress()).save();
            School school = schoolTestPersister.builder().save();
            storeSchoolTestPersister.builder().store(storeA).school(school).save();
            storeSchoolTestPersister.builder().store(storeB).school(school).save();
            Review reviewA = reviewTestPersister.builder().store(storeA).save();
            Review reviewB = reviewTestPersister.builder().store(storeA).save();
            Review reviewC = reviewTestPersister.builder().store(storeB).save();
            MapCoordinateBoundDto mapCoordinateBoundDto = MapCoordinateBoundDto.of(
                    BigDecimal.valueOf(storeA.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(storeA.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewCustomRepository.findPaginationReviewsBySchoolInMapBounds(
                    school.getId(),
                    null,
                    mapCoordinateBoundDto,
                    categoryA.getCategoryType(),
                    2
            );

            assertSoftly(softly -> {
                softly.assertThat(result).containsExactly(reviewB, reviewA);
                softly.assertThat(result).doesNotContain(reviewC);
            });
        }
    }
}
