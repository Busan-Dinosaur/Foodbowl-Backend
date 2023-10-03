package org.dinosaur.foodbowl.domain.review.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.domain.ReviewPhoto;
import org.dinosaur.foodbowl.domain.review.dto.request.DeviceCoordinateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.MapCoordinateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewUpdateRequest;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewPageResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewResponse;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewRepository;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.vo.Address;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.dinosaur.foodbowl.global.util.PointUtils;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.dinosaur.foodbowl.test.file.FileTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("NonAsciiCharacters")
class ReviewServiceTest extends IntegrationTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewPhotoService reviewPhotoService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Nested
    class 북마크한_가게_리뷰_목록_페이징_조회_시 {

        @Test
        void 리뷰_작성자의_팔로워_수도_함께_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().member(writer).store(store).save();
            followTestPersister.builder().following(writer).save();
            followTestPersister.builder().following(writer).save();
            bookmarkTestPersister.builder().member(member).store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            ReviewPageResponse response = reviewService.getReviewsByBookmarkInMapBounds(
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    member
            );

            List<ReviewResponse> result = response.reviews();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).writer().id()).isEqualTo(writer.getId());
                softly.assertThat(result.get(0).writer().nickname()).isEqualTo(writer.getNickname());
                softly.assertThat(result.get(0).writer().followerCount()).isEqualTo(2);
            });
        }

        @Test
        void 리뷰의_사진_목록도_함께_조회한다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().store(store).save();
            ReviewPhoto reviewPhotoA = reviewPhotoTestPersister.builder().review(review).save();
            ReviewPhoto reviewPhotoB = reviewPhotoTestPersister.builder().review(review).save();
            bookmarkTestPersister.builder().member(member).store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            ReviewPageResponse response = reviewService.getReviewsByBookmarkInMapBounds(
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    member
            );

            List<ReviewResponse> result = response.reviews();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).review().id()).isEqualTo(review.getId());
                softly.assertThat(result.get(0).review().content()).isEqualTo(review.getContent());
                softly.assertThat(result.get(0).review().imagePaths())
                        .containsExactly(reviewPhotoA.getPhoto().getPath(), reviewPhotoB.getPhoto().getPath());
                softly.assertThat(result.get(0).review().createdAt()).isEqualTo(review.getCreatedAt());
                softly.assertThat(result.get(0).review().updatedAt()).isEqualTo(review.getUpdatedAt());
            });
        }

        @Test
        void 모든_가게의_북마크_여부는_TRUE_이다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder()
                    .address(
                            Address.of(
                                    "부산광역시 금정구 부산대학로63번길 2",
                                    PointUtils.generate(
                                            BigDecimal.valueOf(129.084180374589),
                                            BigDecimal.valueOf(35.23159315706788)
                                    )
                            )
                    )
                    .save();
            Review review = reviewTestPersister.builder().member(writer).store(store).save();
            followTestPersister.builder().following(writer).save();
            followTestPersister.builder().following(writer).save();
            bookmarkTestPersister.builder().member(member).store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(129.0842730512684),
                    BigDecimal.valueOf(35.23038627521815)
            );

            ReviewPageResponse response = reviewService.getReviewsByBookmarkInMapBounds(
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    member
            );

            List<ReviewResponse> result = response.reviews();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).store().id()).isEqualTo(store.getId());
                softly.assertThat(result.get(0).store().categoryName()).isEqualTo(store.getCategory().getName());
                softly.assertThat(result.get(0).store().name()).isEqualTo(store.getStoreName());
                softly.assertThat(result.get(0).store().addressName()).isEqualTo(store.getAddress().getAddressName());
                softly.assertThat(Math.round(result.get(0).store().distance() / 10) * 10).isEqualTo(130);
                softly.assertThat(result.get(0).store().isBookmarked()).isTrue();
            });
        }
    }

    @Nested
    class 팔로잉_하는_멤버의_리뷰_목록_페이징_조회_시 {

        @Test
        void 리뷰_작성자의_팔로워_수도_함께_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            Member follower = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            followTestPersister.builder().following(writer).follower(follower).save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().member(writer).store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            ReviewPageResponse response = reviewService.getReviewsByFollowingInMapBounds(
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    member
            );

            List<ReviewResponse> result = response.reviews();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).writer().id()).isEqualTo(writer.getId());
                softly.assertThat(result.get(0).writer().nickname()).isEqualTo(writer.getNickname());
                softly.assertThat(result.get(0).writer().followerCount()).isEqualTo(2);
            });
        }

        @Test
        void 리뷰의_사진_목록도_함께_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().member(writer).store(store).save();
            ReviewPhoto reviewPhotoA = reviewPhotoTestPersister.builder().review(review).save();
            ReviewPhoto reviewPhotoB = reviewPhotoTestPersister.builder().review(review).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            ReviewPageResponse response = reviewService.getReviewsByFollowingInMapBounds(
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    member
            );

            List<ReviewResponse> result = response.reviews();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).review().id()).isEqualTo(review.getId());
                softly.assertThat(result.get(0).review().content()).isEqualTo(review.getContent());
                softly.assertThat(result.get(0).review().imagePaths())
                        .containsExactly(reviewPhotoA.getPhoto().getPath(), reviewPhotoB.getPhoto().getPath());
                softly.assertThat(result.get(0).review().createdAt()).isEqualTo(review.getCreatedAt());
                softly.assertThat(result.get(0).review().updatedAt()).isEqualTo(review.getUpdatedAt());
            });
        }

        @Test
        void 북마크한_가게는_북마크_여부가_TRUE_이다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder()
                    .address(
                            Address.of(
                                    "부산광역시 금정구 부산대학로63번길 2",
                                    PointUtils.generate(
                                            BigDecimal.valueOf(129.084180374589),
                                            BigDecimal.valueOf(35.23159315706788)
                                    )
                            )
                    )
                    .save();
            bookmarkTestPersister.builder().member(member).store(store).save();
            reviewTestPersister.builder().member(writer).store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(129.0842730512684),
                    BigDecimal.valueOf(35.23038627521815)
            );

            ReviewPageResponse response = reviewService.getReviewsByFollowingInMapBounds(
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    member
            );

            List<ReviewResponse> result = response.reviews();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).store().id()).isEqualTo(store.getId());
                softly.assertThat(result.get(0).store().categoryName()).isEqualTo(store.getCategory().getName());
                softly.assertThat(result.get(0).store().name()).isEqualTo(store.getStoreName());
                softly.assertThat(result.get(0).store().addressName()).isEqualTo(store.getAddress().getAddressName());
                softly.assertThat(Math.round(result.get(0).store().distance() / 10) * 10).isEqualTo(130);
                softly.assertThat(result.get(0).store().isBookmarked()).isTrue();
            });
        }

        @Test
        void 북마크하지_않은_가게는_북마크_여부가_FALSE_이다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder()
                    .address(
                            Address.of(
                                    "부산광역시 금정구 부산대학로63번길 2",
                                    PointUtils.generate(
                                            BigDecimal.valueOf(129.084180374589),
                                            BigDecimal.valueOf(35.23159315706788)
                                    )
                            )
                    )
                    .save();
            reviewTestPersister.builder().member(writer).store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(129.0842730512684),
                    BigDecimal.valueOf(35.23038627521815)
            );

            ReviewPageResponse response = reviewService.getReviewsByFollowingInMapBounds(
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    member
            );

            List<ReviewResponse> result = response.reviews();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).store().id()).isEqualTo(store.getId());
                softly.assertThat(result.get(0).store().categoryName()).isEqualTo(store.getCategory().getName());
                softly.assertThat(result.get(0).store().name()).isEqualTo(store.getStoreName());
                softly.assertThat(result.get(0).store().addressName()).isEqualTo(store.getAddress().getAddressName());
                softly.assertThat(Math.round(result.get(0).store().distance() / 10) * 10).isEqualTo(130);
                softly.assertThat(result.get(0).store().isBookmarked()).isFalse();
            });
        }
    }

    @Nested
    class 학교_근처_리뷰_목록_페이징_조회_시 {

        @Test
        void 존재하지_않는_학교라면_예외를_던진다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            assertThatThrownBy(() -> reviewService.getReviewsBySchoolInMapBounds(
                    9999L,
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    member
            ))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("존재하지 않는 학교입니다.");
        }

        @Test
        void 리뷰_작성자의_팔로워_수도_함께_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).save();
            followTestPersister.builder().following(writer).save();
            Store store = storeTestPersister.builder().save();
            School school = schoolTestPersister.builder().save();
            storeSchoolTestPersister.builder().store(store).school(school).save();
            Review review = reviewTestPersister.builder().member(writer).store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            ReviewPageResponse response = reviewService.getReviewsBySchoolInMapBounds(
                    school.getId(),
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    member
            );

            List<ReviewResponse> result = response.reviews();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).writer().id()).isEqualTo(writer.getId());
                softly.assertThat(result.get(0).writer().nickname()).isEqualTo(writer.getNickname());
                softly.assertThat(result.get(0).writer().followerCount()).isEqualTo(2);
            });
        }

        @Test
        void 리뷰의_사진_목록도_함께_조회한다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            School school = schoolTestPersister.builder().save();
            storeSchoolTestPersister.builder().store(store).school(school).save();
            Review review = reviewTestPersister.builder().store(store).save();
            ReviewPhoto reviewPhotoA = reviewPhotoTestPersister.builder().review(review).save();
            ReviewPhoto reviewPhotoB = reviewPhotoTestPersister.builder().review(review).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            ReviewPageResponse response = reviewService.getReviewsBySchoolInMapBounds(
                    school.getId(),
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    member
            );

            List<ReviewResponse> result = response.reviews();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).review().id()).isEqualTo(review.getId());
                softly.assertThat(result.get(0).review().content()).isEqualTo(review.getContent());
                softly.assertThat(result.get(0).review().imagePaths())
                        .containsExactly(reviewPhotoA.getPhoto().getPath(), reviewPhotoB.getPhoto().getPath());
                softly.assertThat(result.get(0).review().createdAt()).isEqualTo(review.getCreatedAt());
                softly.assertThat(result.get(0).review().updatedAt()).isEqualTo(review.getUpdatedAt());
            });
        }

        @Test
        void 북마크한_가게는_북마크_여부가_TRUE_이다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder()
                    .address(
                            Address.of(
                                    "부산광역시 금정구 부산대학로63번길 2",
                                    PointUtils.generate(
                                            BigDecimal.valueOf(129.084180374589),
                                            BigDecimal.valueOf(35.23159315706788)
                                    )
                            )
                    )
                    .save();
            bookmarkTestPersister.builder().member(member).store(store).save();
            School school = schoolTestPersister.builder().save();
            storeSchoolTestPersister.builder().store(store).school(school).save();
            Review review = reviewTestPersister.builder().store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(129.0842730512684),
                    BigDecimal.valueOf(35.23038627521815)
            );

            ReviewPageResponse response = reviewService.getReviewsBySchoolInMapBounds(
                    school.getId(),
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    member
            );

            List<ReviewResponse> result = response.reviews();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).store().id()).isEqualTo(store.getId());
                softly.assertThat(result.get(0).store().categoryName()).isEqualTo(store.getCategory().getName());
                softly.assertThat(result.get(0).store().name()).isEqualTo(store.getStoreName());
                softly.assertThat(result.get(0).store().addressName()).isEqualTo(store.getAddress().getAddressName());
                softly.assertThat(Math.round(result.get(0).store().distance() / 10) * 10).isEqualTo(130);
                softly.assertThat(result.get(0).store().isBookmarked()).isTrue();
            });
        }

        @Test
        void 북마크하지_않은_가게는_북마크_여부가_FALSE_이다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder()
                    .address(
                            Address.of(
                                    "부산광역시 금정구 부산대학로63번길 2",
                                    PointUtils.generate(
                                            BigDecimal.valueOf(129.084180374589),
                                            BigDecimal.valueOf(35.23159315706788)
                                    )
                            )
                    )
                    .save();
            School school = schoolTestPersister.builder().save();
            storeSchoolTestPersister.builder().store(store).school(school).save();
            Review review = reviewTestPersister.builder().store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(129.0842730512684),
                    BigDecimal.valueOf(35.23038627521815)
            );

            ReviewPageResponse response = reviewService.getReviewsBySchoolInMapBounds(
                    school.getId(),
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    member
            );

            List<ReviewResponse> result = response.reviews();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).store().id()).isEqualTo(store.getId());
                softly.assertThat(result.get(0).store().categoryName()).isEqualTo(store.getCategory().getName());
                softly.assertThat(result.get(0).store().name()).isEqualTo(store.getStoreName());
                softly.assertThat(result.get(0).store().addressName()).isEqualTo(store.getAddress().getAddressName());
                softly.assertThat(Math.round(result.get(0).store().distance() / 10) * 10).isEqualTo(130);
                softly.assertThat(result.get(0).store().isBookmarked()).isFalse();
            });
        }
    }

    @Nested
    class 리뷰_저장_시 {

        @Test
        void 사진_없이_저장한다() {
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();

            Long reviewId = reviewService.create(reviewCreateRequest, null, member).getId();

            assertThat(reviewId).isNotNull();
        }

        @Test
        void 사진을_포함해서_저장한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();

            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member).getId();

            assertThat(reviewId).isNotNull();
            FileTestUtils.cleanUp();
        }

        @Test
        void 이미_생성된_가게인_경우에도_정상적으로_저장한다() {
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();
            reviewService.create(reviewCreateRequest, null, member);
            ReviewCreateRequest otherReviewCreateRequest = generateReviewCreateRequest();
            Member otherMember = memberTestPersister.builder().save();

            Long savedReviewId = reviewService.create(otherReviewCreateRequest, null, otherMember).getId();

            assertThat(savedReviewId).isNotNull();
        }

        @Test
        void 사진_개수가_최대_사진_개수를_초과하면_예외가_발생한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(5, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();

            assertThatThrownBy(() -> reviewService.create(reviewCreateRequest, multipartFiles, member))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("리뷰에 사진은 최대 4장까지 가능합니다.");
        }
    }

    @Nested
    class 리뷰_수정_시 {

        @Test
        void 사진_수정_없이_정상적으로_수정된다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member).getId();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(Collections.emptyList());

            reviewService.update(reviewId, reviewUpdateRequest, null, member);

            Review updateReview = reviewRepository.findById(reviewId).get();
            assertSoftly(softly -> {
                softly.assertThat(updateReview.getContent()).isEqualTo(reviewUpdateRequest.reviewContent());
                softly.assertThat(reviewPhotoService.findPhotos(updateReview)).hasSize(multipartFiles.size());
            });
            FileTestUtils.cleanUp();
        }

        @Test
        void 사진을_새로_추가하고_정상적으로_수정된다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member).getId();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(Collections.emptyList());
            List<MultipartFile> updateImages = FileTestUtils.generateMultipartFiles(2, "images");

            reviewService.update(reviewId, reviewUpdateRequest, updateImages, member);

            Review updateReview = reviewRepository.findById(reviewId).get();
            int updatePhotoSize = multipartFiles.size() + updateImages.size();
            assertSoftly(softly -> {
                softly.assertThat(updateReview.getContent()).isEqualTo(reviewUpdateRequest.reviewContent());
                softly.assertThat(reviewPhotoService.findPhotos(updateReview)).hasSize(updatePhotoSize);
            });
            FileTestUtils.cleanUp();
        }

        @Test
        void 기존_사진을_삭제하고_정상적으로_수정된다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();
            Review review = reviewService.create(reviewCreateRequest, multipartFiles, member);
            List<Photo> reviewPhotos = reviewPhotoService.findPhotos(review);
            List<Long> deletePhotoIds = List.of(reviewPhotos.get(0).getId());
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(deletePhotoIds);

            reviewService.update(review.getId(), reviewUpdateRequest, null, member);

            Review updateReview = reviewRepository.findById(review.getId()).get();
            int updatePhotoSize = multipartFiles.size() - deletePhotoIds.size();
            assertSoftly(softly -> {
                softly.assertThat(updateReview.getContent()).isEqualTo(reviewUpdateRequest.reviewContent());
                softly.assertThat(reviewPhotoService.findPhotos(updateReview)).hasSize(updatePhotoSize);
            });
            FileTestUtils.cleanUp();
        }

        @Test
        void 기존_사진을_삭제하고_사진을_새롭게_추가하며_정상적으로_수정된다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();
            Review review = reviewService.create(reviewCreateRequest, multipartFiles, member);
            List<Photo> reviewPhotos = reviewPhotoService.findPhotos(review);
            List<Long> deletePhotoIds = List.of(reviewPhotos.get(0).getId());
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(deletePhotoIds);
            List<MultipartFile> updateImages = FileTestUtils.generateMultipartFiles(2, "images");

            reviewService.update(review.getId(), reviewUpdateRequest, updateImages, member);

            Review updateReview = reviewRepository.findById(review.getId()).get();
            int updatePhotoSize = multipartFiles.size() + updateImages.size() - deletePhotoIds.size();
            assertSoftly(softly -> {
                softly.assertThat(updateReview.getContent()).isEqualTo(reviewUpdateRequest.reviewContent());
                softly.assertThat(reviewPhotoService.findPhotos(updateReview)).hasSize(updatePhotoSize);
            });
            FileTestUtils.cleanUp();
        }

        @Test
        void 존재하지_않는_리뷰이면_예외가_발생한다() {
            Member member = memberTestPersister.builder().save();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(Collections.emptyList());

            assertThatThrownBy(() -> reviewService.update(-1L, reviewUpdateRequest, null, member))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("일치하는 리뷰를 찾을 수 없습니다.");
        }

        @Test
        void 작성자가_아니면_예외가_발생한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();
            Member otherMember = memberTestPersister.builder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member).getId();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(Collections.emptyList());

            assertThatThrownBy(() -> reviewService.update(reviewId, reviewUpdateRequest, null, otherMember))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("본인이 작성한 리뷰가 아닙니다.");
        }

        @Test
        void 삭제하려는_사진이_리뷰의_사진이_아니면_예외가_발생한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member).getId();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(List.of(-1L));
            List<MultipartFile> updateImages = FileTestUtils.generateMultipartFiles(3, "images");

            assertThatThrownBy(() -> reviewService.update(reviewId, reviewUpdateRequest, updateImages, member))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("삭제하려는 사진이 현재 리뷰에 존재하지 않습니다.");
        }

        @Test
        void 사진_개수가_최대_사진_개수를_초과하면_예외가_발생한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member).getId();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(Collections.emptyList());
            List<MultipartFile> updateImages = FileTestUtils.generateMultipartFiles(3, "images");

            assertThatThrownBy(() -> reviewService.update(reviewId, reviewUpdateRequest, updateImages, member))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("리뷰에 사진은 최대 4장까지 가능합니다.");
        }
    }

    @Nested
    class 리뷰_삭제_시 {

        @Test
        void 정상적인_요청이라면_리뷰를_삭제한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member).getId();

            reviewService.delete(reviewId, member);

            assertThat(reviewRepository.findById(reviewId)).isEmpty();
        }

        @Test
        void 등록된_리뷰가_아니면_예외를_던진다() {
            Member member = memberTestPersister.builder().save();

            assertThatThrownBy(() -> reviewService.delete(-1L, member))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("일치하는 리뷰를 찾을 수 없습니다.");
        }

        @Test
        void 리뷰_작성자가_아니면_예외를_던진다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();
            Member otherMember = memberTestPersister.builder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member).getId();

            assertThatThrownBy(() -> reviewService.delete(reviewId, otherMember))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("본인이 작성한 리뷰가 아닙니다.");
        }
    }

    private ReviewCreateRequest generateReviewCreateRequest() {
        return new ReviewCreateRequest(
                "2321515",
                "신천직화집",
                "서울시 강남구 테헤란로 90, 13층",
                BigDecimal.valueOf(125.1234),
                BigDecimal.valueOf(37.24455),
                "http://store.url",
                "02-1234-2424",
                "한식",
                "맛있습니다.",
                "부산대학교",
                "부산광역시 금정구 부산대학로63번길 2",
                BigDecimal.valueOf(124.1234),
                BigDecimal.valueOf(34.545)
        );
    }

    private ReviewUpdateRequest generateReviewUpdateRequest(List<Long> deletePhotoIds) {
        return new ReviewUpdateRequest(
                "애들이 먹기에는 조금 매워요",
                deletePhotoIds
        );
    }
}
