package org.dinosaur.foodbowl.domain.review.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.application.dto.CoordinateBoundDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.domain.ReviewPhoto;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ReviewPhotoCustomRepositoryImplTest extends PersistenceTest {

    @Autowired
    private ReviewPhotoRepository reviewPhotoRepository;

    @Autowired
    private ReviewPhotoCustomRepositoryImpl reviewPhotoCustomRepositoryImpl;

    @Nested
    class 팔로잉_유저의_리뷰_목록_페이징_조회_시 {

        @Test
        void 마지막_리뷰ID가_NULL이_아닐때_마지막_리뷰ID보다_작은_리뷰는_조회한다() {
            Member member = memberTestPersister.memberBuilder().save();
            Member writer = memberTestPersister.memberBuilder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().store(store).member(writer).save();
            CoordinateBoundDto coordinateBoundDto = CoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewPhotoCustomRepositoryImpl.getPaginationReviewsByFollowing(
                    member.getId(),
                    review.getId() + 1,
                    coordinateBoundDto,
                    10
            );

            assertThat(result).containsExactly(review);
        }

        @Test
        void 마지막_리뷰ID가_NULL이_아닐때_마지막_리뷰ID보다_큰_리뷰는_조회하지_않는다() {
            Member member = memberTestPersister.memberBuilder().save();
            Member writer = memberTestPersister.memberBuilder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().store(store).member(writer).save();
            CoordinateBoundDto coordinateBoundDto = CoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewPhotoCustomRepositoryImpl.getPaginationReviewsByFollowing(
                    member.getId(),
                    review.getId() - 1,
                    coordinateBoundDto,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 팔로잉_하고있는_유저의_리뷰를_조회한다() {
            Member member = memberTestPersister.memberBuilder().save();
            Member writer = memberTestPersister.memberBuilder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().store(store).member(writer).save();
            CoordinateBoundDto coordinateBoundDto = CoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewPhotoCustomRepositoryImpl.getPaginationReviewsByFollowing(
                    member.getId(),
                    null,
                    coordinateBoundDto,
                    10
            );

            assertThat(result).containsExactly(review);
        }

        @Test
        void 팔로잉_하고있지_않은_유저의_리뷰는_조회하지_않는다() {
            Member member = memberTestPersister.memberBuilder().save();
            Member writer = memberTestPersister.memberBuilder().save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().store(store).member(writer).save();
            CoordinateBoundDto coordinateBoundDto = CoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewPhotoCustomRepositoryImpl.getPaginationReviewsByFollowing(
                    member.getId(),
                    null,
                    coordinateBoundDto,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 폴리곤_영역에_경도가_속하지_않는_가게의_리뷰는_조회하지_않는다() {
            Member member = memberTestPersister.memberBuilder().save();
            Member writer = memberTestPersister.memberBuilder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().store(store).member(writer).save();
            CoordinateBoundDto coordinateBoundDto = CoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX() + 10),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Review> result = reviewPhotoCustomRepositoryImpl.getPaginationReviewsByFollowing(
                    member.getId(),
                    null,
                    coordinateBoundDto,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 폴리곤_영역에_위도가_속하지_않는_가게의_리뷰는_조회하지_않는다() {
            Member member = memberTestPersister.memberBuilder().save();
            Member writer = memberTestPersister.memberBuilder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().store(store).member(writer).save();
            CoordinateBoundDto coordinateBoundDto = CoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY() + 10),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Review> result = reviewPhotoCustomRepositoryImpl.getPaginationReviewsByFollowing(
                    member.getId(),
                    null,
                    coordinateBoundDto,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 폴리곤_영역에_경도와_위도가_속하지_않는_가게의_리뷰는_조회하지_않는다() {
            Member member = memberTestPersister.memberBuilder().save();
            Member writer = memberTestPersister.memberBuilder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().store(store).member(writer).save();
            CoordinateBoundDto coordinateBoundDto = CoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX() + 10),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY() + 10),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            List<Review> result = reviewPhotoCustomRepositoryImpl.getPaginationReviewsByFollowing(
                    member.getId(),
                    null,
                    coordinateBoundDto,
                    10
            );

            assertThat(result).isEmpty();
        }

        @Test
        void 리뷰ID를_내림차순으로_조회한다() {
            Member member = memberTestPersister.memberBuilder().save();
            Member writer = memberTestPersister.memberBuilder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().store(store).member(writer).save();
            Review reviewB = reviewTestPersister.builder().store(store).member(writer).save();
            CoordinateBoundDto coordinateBoundDto = CoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewPhotoCustomRepositoryImpl.getPaginationReviewsByFollowing(
                    member.getId(),
                    null,
                    coordinateBoundDto,
                    10
            );

            assertThat(result).containsExactly(reviewB, reviewA);
        }

        @Test
        void 페이지_크기만큼_조회한다() {
            Member member = memberTestPersister.memberBuilder().save();
            Member writer = memberTestPersister.memberBuilder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().store(store).member(writer).save();
            Review reviewB = reviewTestPersister.builder().store(store).member(writer).save();
            Review reviewC = reviewTestPersister.builder().store(store).member(writer).save();
            CoordinateBoundDto coordinateBoundDto = CoordinateBoundDto.of(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(3),
                    BigDecimal.valueOf(3)
            );

            List<Review> result = reviewPhotoCustomRepositoryImpl.getPaginationReviewsByFollowing(
                    member.getId(),
                    null,
                    coordinateBoundDto,
                    2
            );

            assertThat(result).containsExactly(reviewC, reviewB);
        }
    }

    @Test
    void 리뷰_사진_엔티티를_삭제한다() {
        Review review = reviewTestPersister.builder().save();
        Photo photo1 = photoTestPersister.builder().save();
        Photo photo2 = photoTestPersister.builder().save();
        ReviewPhoto reviewPhoto1 = ReviewPhoto.builder()
                .review(review)
                .photo(photo1)
                .build();
        ReviewPhoto reviewPhoto2 = ReviewPhoto.builder()
                .review(review)
                .photo(photo2)
                .build();
        reviewPhotoRepository.save(reviewPhoto1);
        reviewPhotoRepository.save(reviewPhoto2);

        long deleteCount = reviewPhotoCustomRepositoryImpl.deleteAllByReview(review);

        assertThat(deleteCount).isEqualTo(2);
    }

    @Test
    void 리뷰에_해당하는_사진_엔티티들을_삭제한다() {
        Review review = reviewTestPersister.builder().save();
        Photo deleteTargetPhotoA = photoTestPersister.builder().save();
        Photo deleteTargetPhotoB = photoTestPersister.builder().save();
        Photo photo = photoTestPersister.builder().save();
        ReviewPhoto deleteReviewPhotoA = ReviewPhoto.builder()
                .review(review)
                .photo(deleteTargetPhotoA)
                .build();
        ReviewPhoto deleteReviewPhotoB = ReviewPhoto.builder()
                .review(review)
                .photo(deleteTargetPhotoB)
                .build();
        ReviewPhoto reviewPhoto = ReviewPhoto.builder()
                .review(review)
                .photo(photo)
                .build();
        reviewPhotoRepository.save(deleteReviewPhotoA);
        reviewPhotoRepository.save(deleteReviewPhotoB);
        reviewPhotoRepository.save(reviewPhoto);

        List<Photo> deleteTargetPhotos = List.of(deleteTargetPhotoA, deleteTargetPhotoB);
        long deleteCount = reviewPhotoCustomRepositoryImpl.deleteByReviewAndPhotos(review, deleteTargetPhotos);

        assertSoftly(softly -> {
            softly.assertThat(deleteCount).isEqualTo(deleteTargetPhotos.size());
            softly.assertThat(reviewPhotoRepository.findAllByReview(review)).containsExactly(reviewPhoto);
            softly.assertThat(reviewPhotoRepository.findAllByReview(review))
                    .doesNotContain(deleteReviewPhotoA, deleteReviewPhotoB);
        });
    }
}
