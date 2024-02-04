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
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewFeedPageResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewFeedResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewPageInfo;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewPageResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewStoreResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.StoreReviewContentResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.StoreReviewResponse;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewRepository;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.vo.Address;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.dinosaur.foodbowl.global.presentation.LoginMember;
import org.dinosaur.foodbowl.global.util.PointUtils;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.dinosaur.foodbowl.test.file.FileTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    class 리뷰_단건_조회_시 {

        @Test
        void 가게_북마크_여부를_함께_조회한다() {
            Member loginMember = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            bookmarkTestPersister.builder().member(loginMember).store(store).save();
            Review review = reviewTestPersister.builder().member(writer).store(store).content("꿀맛이에요").save();
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            ReviewResponse reviewResponse = reviewService.getReview(
                    review.getId(),
                    new LoginMember(loginMember.getId()),
                    deviceCoordinateRequest
            );

            assertSoftly(softly -> {
                softly.assertThat(reviewResponse.store().id()).isEqualTo(store.getId());
                softly.assertThat(reviewResponse.store().isBookmarked()).isTrue();
                softly.assertThat(reviewResponse.store().storeUrl()).isEqualTo(store.getStoreUrl());
                softly.assertThat(reviewResponse.review().content()).isEqualTo(review.getContent());
                softly.assertThat(reviewResponse.review().imagePaths()).isEmpty();
                softly.assertThat(reviewResponse.writer().id()).isEqualTo(writer.getId());
                softly.assertThat(reviewResponse.writer().nickname()).isEqualTo(writer.getNickname());
                softly.assertThat(reviewResponse.writer().followerCount()).isEqualTo(0);
            });
        }

        @Test
        void 리뷰_작성자_팔로워_수를_함께_조회한다() {
            Member loginMember = memberTestPersister.builder().save();
            Member memberA = memberTestPersister.builder().save();
            Member memberB = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            bookmarkTestPersister.builder().member(loginMember).store(store).save();
            followTestPersister.builder().follower(memberA).following(writer).save();
            followTestPersister.builder().follower(memberB).following(writer).save();
            Review review = reviewTestPersister.builder().member(writer).store(store).content("꿀맛이에요").save();
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            ReviewResponse reviewResponse = reviewService.getReview(
                    review.getId(),
                    new LoginMember(loginMember.getId()),
                    deviceCoordinateRequest
            );

            assertSoftly(softly -> {
                softly.assertThat(reviewResponse.store().id()).isEqualTo(store.getId());
                softly.assertThat(reviewResponse.store().isBookmarked()).isTrue();
                softly.assertThat(reviewResponse.review().content()).isEqualTo(review.getContent());
                softly.assertThat(reviewResponse.review().imagePaths()).isEmpty();
                softly.assertThat(reviewResponse.writer().id()).isEqualTo(writer.getId());
                softly.assertThat(reviewResponse.writer().nickname()).isEqualTo(writer.getNickname());
                softly.assertThat(reviewResponse.writer().followerCount()).isEqualTo(2);
            });
        }

        @Test
        void 리뷰_사진을_함께_조회한다() {
            Member loginMember = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            bookmarkTestPersister.builder().member(loginMember).store(store).save();
            Review review = reviewTestPersister.builder().member(writer).store(store).content("꿀맛이에요").save();
            Photo photoA = photoTestPersister.builder().save();
            Photo photoB = photoTestPersister.builder().save();
            ReviewPhoto reviewPhotoA = reviewPhotoTestPersister.builder().review(review).photo(photoA).save();
            ReviewPhoto reviewPhotoB = reviewPhotoTestPersister.builder().review(review).photo(photoB).save();
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            ReviewResponse reviewResponse = reviewService.getReview(
                    review.getId(),
                    new LoginMember(loginMember.getId()),
                    deviceCoordinateRequest
            );

            assertSoftly(softly -> {
                softly.assertThat(reviewResponse.store().id()).isEqualTo(store.getId());
                softly.assertThat(reviewResponse.store().isBookmarked()).isTrue();
                softly.assertThat(reviewResponse.review().content()).isEqualTo(review.getContent());
                softly.assertThat(reviewResponse.review().imagePaths())
                        .containsExactly(reviewPhotoA.getPhoto().getPath(), reviewPhotoB.getPhoto().getPath());
                softly.assertThat(reviewResponse.writer().id()).isEqualTo(writer.getId());
                softly.assertThat(reviewResponse.writer().nickname()).isEqualTo(writer.getNickname());
                softly.assertThat(reviewResponse.writer().followerCount()).isEqualTo(0);
            });
        }

        @Test
        void 존재하지_않는_리뷰이면_예외를_던진다() {
            Long wrongReviewId = -1L;
            Member member = memberTestPersister.builder().save();
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            assertThatThrownBy(() ->
                    reviewService.getReview(
                            wrongReviewId,
                            new LoginMember(member.getId()),
                            deviceCoordinateRequest
                    ))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("일치하는 리뷰를 찾을 수 없습니다.");
        }
    }

    @Nested
    class 리뷰_피드_조회_시 {

        @Test
        void 사진이_포함된_리뷰를_조회한다() {
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            bookmarkTestPersister.builder().store(store).member(writer).save();
            Review reviewA = reviewTestPersister.builder().member(writer).store(store).content("맛도리").save();
            Photo photoA = photoTestPersister.builder().save();
            Photo photoB = photoTestPersister.builder().save();
            reviewPhotoTestPersister.builder().review(reviewA).photo(photoA).save();
            reviewPhotoTestPersister.builder().review(reviewA).photo(photoB).save();
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            ReviewFeedPageResponse reviewFeedPageResponse = reviewService.getReviewFeeds(
                    null,
                    10,
                    deviceCoordinateRequest,
                    new LoginMember(writer.getId())
            );

            List<ReviewFeedResponse> reviewFeedResponses = reviewFeedPageResponse.reviewFeedResponses();
            assertSoftly(softly -> {
                softly.assertThat(reviewFeedResponses.size()).isOne();
                softly.assertThat(reviewFeedResponses.get(0).review().id()).isEqualTo(reviewA.getId());
                softly.assertThat(reviewFeedResponses.get(0).review().content()).isEqualTo("맛도리");
                softly.assertThat(reviewFeedResponses.get(0).reviewFeedThumbnail()).isEqualTo(photoA.getPath());
                softly.assertThat(reviewFeedResponses.get(0).writer().id()).isEqualTo(writer.getId());
                softly.assertThat(reviewFeedResponses.get(0).store().id()).isEqualTo(store.getId());
                softly.assertThat(reviewFeedResponses.get(0).store().storeUrl()).isEqualTo(store.getStoreUrl());
                softly.assertThat(reviewFeedResponses.get(0).store().isBookmarked()).isTrue();
            });
        }

        @Test
        void 사진이_포함된_리뷰가_없으면_빈_값을_반환한다() {
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().member(writer).store(store).content("사진 없는 리뷰").save();
            DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            ReviewFeedPageResponse reviewFeedPageResponse = reviewService.getReviewFeeds(
                    null,
                    10,
                    deviceCoordinateRequest,
                    new LoginMember(writer.getId())
            );

            assertThat(reviewFeedPageResponse.reviewFeedResponses()).isEmpty();
        }
    }

    @Nested
    class 멤버_리뷰_목록_페이징_조회_시 {

        @Test
        void 존재하지_않은_멤버라면_예외를_던진다() {
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

            assertThatThrownBy(() -> reviewService.getReviewsByMemberInMapBounds(
                    -1L,
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    new LoginMember(member.getId())
            ))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        void 리뷰_작성자의_팔로워_수도_함께_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).save();
            followTestPersister.builder().following(writer).save();
            Store store = storeTestPersister.builder().save();
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

            ReviewPageResponse response = reviewService.getReviewsByMemberInMapBounds(
                    writer.getId(),
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    new LoginMember(member.getId())
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

            ReviewPageResponse response = reviewService.getReviewsByMemberInMapBounds(
                    writer.getId(),
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    new LoginMember(member.getId())
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
            Review review = reviewTestPersister.builder().member(writer).store(store).save();
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

            ReviewPageResponse response = reviewService.getReviewsByMemberInMapBounds(
                    writer.getId(),
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    new LoginMember(member.getId())
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

            ReviewPageResponse response = reviewService.getReviewsByMemberInMapBounds(
                    writer.getId(),
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    new LoginMember(member.getId())
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

        @Test
        void 일치하는_리뷰가_없으면_빈_리스트를_반환한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
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

            ReviewPageResponse response = reviewService.getReviewsByMemberInMapBounds(
                    writer.getId(),
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    new LoginMember(member.getId())
            );

            assertSoftly(softly -> {
                softly.assertThat(response.reviews()).isEmpty();
                softly.assertThat(response.page().firstId()).isNull();
                softly.assertThat(response.page().lastId()).isNull();
                softly.assertThat(response.page().size()).isZero();
            });
        }
    }

    @Nested
    class 가게에_해당하는_리뷰_목록_페이징_조회_시 {

        @Test
        void 모든_리뷰를_조회한다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review reviewA = reviewTestPersister.builder().store(store).content("맛있어요").save();
            Review reviewB = reviewTestPersister.builder().store(store).content("맛없어요").save();

            StoreReviewResponse storeReviewResponse = reviewService.getReviewsByStore(
                    store.getId(),
                    "ALL",
                    null,
                    10,
                    new DeviceCoordinateRequest(BigDecimal.valueOf(124.124), BigDecimal.valueOf(37.41424)),
                    new LoginMember(member.getId())
            );

            List<StoreReviewContentResponse> reviewContentResponses = storeReviewResponse.storeReviewContentResponses();
            ReviewStoreResponse reviewStoreResponse = storeReviewResponse.reviewStoreResponse();
            ReviewPageInfo reviewPageInfo = storeReviewResponse.page();
            assertSoftly(softly -> {
                softly.assertThat(reviewContentResponses).hasSize(2);
                softly.assertThat(reviewContentResponses.get(0).review().id()).isEqualTo(reviewB.getId());
                softly.assertThat(reviewContentResponses.get(0).review().content()).isEqualTo(reviewB.getContent());
                softly.assertThat(reviewContentResponses.get(1).review().id()).isEqualTo(reviewA.getId());
                softly.assertThat(reviewContentResponses.get(1).review().content()).isEqualTo(reviewA.getContent());
                softly.assertThat(reviewPageInfo.size()).isEqualTo(2);
                softly.assertThat(reviewPageInfo.firstId()).isEqualTo(reviewB.getId());
                softly.assertThat(reviewPageInfo.lastId()).isEqualTo(reviewA.getId());
                softly.assertThat(reviewStoreResponse.id()).isEqualTo(store.getId());
                softly.assertThat(reviewStoreResponse.name()).isEqualTo(store.getStoreName());
                softly.assertThat(reviewStoreResponse.addressName()).isEqualTo(store.getAddress().getAddressName());
                softly.assertThat(reviewStoreResponse.storeUrl()).isEqualTo(store.getStoreUrl());
                softly.assertThat(reviewStoreResponse.isBookmarked()).isFalse();
            });
        }

        @Test
        void 북마크한_가게는_북마크_여부가_TRUE_이다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            bookmarkTestPersister.builder().member(member).store(store).save();
            Review reviewA = reviewTestPersister.builder().store(store).content("맛있어요").save();
            Review reviewB = reviewTestPersister.builder().store(store).content("맛없어요").save();

            StoreReviewResponse storeReviewResponse = reviewService.getReviewsByStore(
                    store.getId(),
                    "ALL",
                    null,
                    10,
                    new DeviceCoordinateRequest(BigDecimal.valueOf(124.124), BigDecimal.valueOf(37.41424)),
                    new LoginMember(member.getId())
            );

            List<StoreReviewContentResponse> reviewContentResponses = storeReviewResponse.storeReviewContentResponses();
            ReviewStoreResponse reviewStoreResponse = storeReviewResponse.reviewStoreResponse();
            ReviewPageInfo reviewPageInfo = storeReviewResponse.page();
            assertSoftly(softly -> {
                softly.assertThat(reviewContentResponses).hasSize(2);
                softly.assertThat(reviewContentResponses.get(0).review().id()).isEqualTo(reviewB.getId());
                softly.assertThat(reviewContentResponses.get(0).review().content()).isEqualTo(reviewB.getContent());
                softly.assertThat(reviewContentResponses.get(1).review().id()).isEqualTo(reviewA.getId());
                softly.assertThat(reviewContentResponses.get(1).review().content()).isEqualTo(reviewA.getContent());
                softly.assertThat(reviewPageInfo.size()).isEqualTo(2);
                softly.assertThat(reviewPageInfo.firstId()).isEqualTo(reviewB.getId());
                softly.assertThat(reviewPageInfo.lastId()).isEqualTo(reviewA.getId());
                softly.assertThat(reviewStoreResponse.id()).isEqualTo(store.getId());
                softly.assertThat(reviewStoreResponse.name()).isEqualTo(store.getStoreName());
                softly.assertThat(reviewStoreResponse.addressName()).isEqualTo(store.getAddress().getAddressName());
                softly.assertThat(reviewStoreResponse.isBookmarked()).isTrue();
            });
        }

        @Test
        void 팔로워_리뷰만_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            followTestPersister.builder().follower(member).following(writer).save();
            Review reviewA = reviewTestPersister.builder().store(store).member(writer).content("맛있어요").save();
            Review reviewB = reviewTestPersister.builder().store(store).content("맛없어요").save();

            StoreReviewResponse storeReviewResponse = reviewService.getReviewsByStore(
                    store.getId(),
                    "FRIEND",
                    null,
                    10,
                    new DeviceCoordinateRequest(BigDecimal.valueOf(124.124), BigDecimal.valueOf(37.41424)),
                    new LoginMember(member.getId())
            );

            List<StoreReviewContentResponse> reviewContentResponses = storeReviewResponse.storeReviewContentResponses();
            ReviewPageInfo reviewPageInfo = storeReviewResponse.page();
            assertSoftly(softly -> {
                softly.assertThat(reviewContentResponses).hasSize(1);
                softly.assertThat(reviewContentResponses.get(0).review().id()).isEqualTo(reviewA.getId());
                softly.assertThat(reviewContentResponses.get(0).review().content()).isEqualTo(reviewA.getContent());
                softly.assertThat(reviewPageInfo.size()).isEqualTo(1);
                softly.assertThat(reviewPageInfo.firstId()).isEqualTo(reviewA.getId());
                softly.assertThat(reviewPageInfo.lastId()).isEqualTo(reviewA.getId());
            });
        }

        @Test
        void 리뷰_작성자의_팔로워_수도_함께_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            Review review = reviewTestPersister.builder().member(writer).store(store).save();
            followTestPersister.builder().following(writer).save();
            followTestPersister.builder().following(writer).save();

            StoreReviewResponse storeReviewResponse = reviewService.getReviewsByStore(
                    store.getId(),
                    "ALL",
                    null,
                    10,
                    new DeviceCoordinateRequest(BigDecimal.valueOf(124.124), BigDecimal.valueOf(37.41424)),
                    new LoginMember(member.getId())
            );

            List<StoreReviewContentResponse> result = storeReviewResponse.storeReviewContentResponses();
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

            StoreReviewResponse storeReviewResponse = reviewService.getReviewsByStore(
                    store.getId(),
                    "ALL",
                    null,
                    10,
                    new DeviceCoordinateRequest(BigDecimal.valueOf(124.124), BigDecimal.valueOf(37.41424)),
                    new LoginMember(member.getId())
            );

            List<StoreReviewContentResponse> result = storeReviewResponse.storeReviewContentResponses();
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
        void 해당_가게가_존재하지_않으면_예외가_발생한다() {
            assertThatThrownBy(() -> reviewService.getReviewsByStore(
                    -1L,
                    "ALL",
                    null,
                    10,
                    null,
                    null
            ))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("일치하는 가게를 찾을 수 없습니다.");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "test", "all", "friend"})
        void 일치하는_리뷰_필터링_조건이_없으면_예외가_발생한다(String reviewFilter) {
            Store store = storeTestPersister.builder().save();
            Member member = memberTestPersister.builder().save();

            assertThatThrownBy(() -> reviewService.getReviewsByStore(
                    store.getId(),
                    reviewFilter,
                    null,
                    10,
                    null,
                    new LoginMember(member.getId())
            ))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("일치하는 리뷰 필터링 조건이 없습니다.");
        }
    }

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
                    new LoginMember(member.getId())
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
                    new LoginMember(member.getId())
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
                    new LoginMember(member.getId())
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
                    new LoginMember(member.getId())
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
                    new LoginMember(member.getId())
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
                    new LoginMember(member.getId())
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
                    new LoginMember(member.getId())
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
                    -1L,
                    null,
                    mapCoordinateRequest,
                    deviceCoordinateRequest,
                    10,
                    new LoginMember(member.getId())
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
                    new LoginMember(member.getId())
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
                    new LoginMember(member.getId())
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
                    new LoginMember(member.getId())
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
                    new LoginMember(member.getId())
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

            Long reviewId = reviewService.create(reviewCreateRequest, null, new LoginMember(member.getId()))
                    .getId();

            assertThat(reviewId).isNotNull();
        }

        @Test
        void 사진을_포함해서_저장한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();

            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, new LoginMember(member.getId()))
                    .getId();

            assertThat(reviewId).isNotNull();
            FileTestUtils.cleanUp();
        }

        @Test
        void 이미_생성된_가게인_경우에도_정상적으로_저장한다() {
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();
            reviewService.create(reviewCreateRequest, null, new LoginMember(member.getId()));
            ReviewCreateRequest otherReviewCreateRequest = generateReviewCreateRequest();
            Member otherMember = memberTestPersister.builder().save();

            Long savedReviewId =
                    reviewService.create(otherReviewCreateRequest, null, new LoginMember(otherMember.getId()))
                            .getId();

            assertThat(savedReviewId).isNotNull();
        }

        @Test
        void 사진_개수가_최대_사진_개수를_초과하면_예외가_발생한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(5, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();

            assertThatThrownBy(
                    () -> reviewService.create(reviewCreateRequest, multipartFiles, new LoginMember(member.getId())))
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
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, new LoginMember(member.getId()))
                    .getId();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(Collections.emptyList());

            reviewService.update(reviewId, reviewUpdateRequest, null, new LoginMember(member.getId()));

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
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, new LoginMember(member.getId()))
                    .getId();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(Collections.emptyList());
            List<MultipartFile> updateImages = FileTestUtils.generateMultipartFiles(2, "images");

            reviewService.update(reviewId, reviewUpdateRequest, updateImages, new LoginMember(member.getId()));

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
            Review review = reviewService.create(reviewCreateRequest, multipartFiles, new LoginMember(member.getId()));
            List<Photo> reviewPhotos = reviewPhotoService.findPhotos(review);
            List<Long> deletePhotoIds = List.of(reviewPhotos.get(0).getId());
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(deletePhotoIds);

            reviewService.update(review.getId(), reviewUpdateRequest, null, new LoginMember(member.getId()));

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
            Review review = reviewService.create(reviewCreateRequest, multipartFiles, new LoginMember(member.getId()));
            List<Photo> reviewPhotos = reviewPhotoService.findPhotos(review);
            List<Long> deletePhotoIds = List.of(reviewPhotos.get(0).getId());
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(deletePhotoIds);
            List<MultipartFile> updateImages = FileTestUtils.generateMultipartFiles(2, "images");

            reviewService.update(review.getId(), reviewUpdateRequest, updateImages, new LoginMember(member.getId()));

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

            assertThatThrownBy(
                    () -> reviewService.update(-1L, reviewUpdateRequest, null, new LoginMember(member.getId())))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("일치하는 리뷰를 찾을 수 없습니다.");
        }

        @Test
        void 작성자가_아니면_예외가_발생한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();
            Member otherMember = memberTestPersister.builder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, new LoginMember(member.getId()))
                    .getId();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(Collections.emptyList());

            assertThatThrownBy(() ->
                    reviewService.update(
                            reviewId,
                            reviewUpdateRequest,
                            null,
                            new LoginMember(otherMember.getId())
                    ))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("본인이 작성한 리뷰가 아닙니다.");
        }

        @Test
        void 삭제하려는_사진이_리뷰의_사진이_아니면_예외가_발생한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, new LoginMember(member.getId()))
                    .getId();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(List.of(-1L));
            List<MultipartFile> updateImages = FileTestUtils.generateMultipartFiles(3, "images");

            assertThatThrownBy(() ->
                    reviewService.update(
                            reviewId,
                            reviewUpdateRequest,
                            updateImages,
                            new LoginMember(member.getId())
                    ))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("삭제하려는 사진이 현재 리뷰에 존재하지 않습니다.");
        }

        @Test
        void 사진_개수가_최대_사진_개수를_초과하면_예외가_발생한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, new LoginMember(member.getId()))
                    .getId();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(Collections.emptyList());
            List<MultipartFile> updateImages = FileTestUtils.generateMultipartFiles(3, "images");

            assertThatThrownBy(() ->
                    reviewService.update(
                            reviewId,
                            reviewUpdateRequest,
                            updateImages,
                            new LoginMember(member.getId())
                    ))
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
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, new LoginMember(member.getId()))
                    .getId();

            reviewService.delete(reviewId, new LoginMember(member.getId()));

            assertThat(reviewRepository.findById(reviewId)).isEmpty();
        }

        @Test
        void 등록된_리뷰가_아니면_예외를_던진다() {
            Member member = memberTestPersister.builder().save();

            assertThatThrownBy(() -> reviewService.delete(-1L, new LoginMember(member.getId())))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("일치하는 리뷰를 찾을 수 없습니다.");
        }

        @Test
        void 리뷰_작성자가_아니면_예외를_던진다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2, "images");
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.builder().save();
            Member otherMember = memberTestPersister.builder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, new LoginMember(member.getId()))
                    .getId();

            assertThatThrownBy(() -> reviewService.delete(reviewId, new LoginMember(otherMember.getId())))
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
