package org.dinosaur.foodbowl.domain.review.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.application.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.domain.review.application.dto.request.ReviewUpdateRequest;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewRepository;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
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
    class 리뷰_저장_시 {

        @Test
        void 사진_없이_저장한다() {
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();

            Long reviewId = reviewService.create(reviewCreateRequest, null, member).getId();

            assertThat(reviewId).isNotNull();
        }

        @Test
        void 사진을_포함해서_저장한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();

            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member).getId();

            assertThat(reviewId).isNotNull();
            FileTestUtils.cleanUp();
        }

        @Test
        void 이미_생성된_가게인_경우에도_정상적으로_저장한다() {
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();
            reviewService.create(reviewCreateRequest, null, member);
            ReviewCreateRequest otherReviewCreateRequest = generateReviewCreateRequest();
            Member otherMember = memberTestPersister.memberBuilder().save();

            Long savedReviewId = reviewService.create(otherReviewCreateRequest, null, otherMember).getId();

            assertThat(savedReviewId).isNotNull();
        }

        @Test
        void 사진_개수가_최대_사진_개수를_초과하면_예외가_발생한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(5);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();

            assertThatThrownBy(() -> reviewService.create(reviewCreateRequest, multipartFiles, member))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("리뷰에 사진은 최대 4장까지 가능합니다.");
        }
    }

    @Nested
    class 리뷰_수정_시 {

        @Test
        void 사진_수정_없이_정상적으로_수정된다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();
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
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member).getId();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(Collections.emptyList());
            List<MultipartFile> updateImages = FileTestUtils.generateMultipartFiles(2);

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
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();
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
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();
            Review review = reviewService.create(reviewCreateRequest, multipartFiles, member);
            List<Photo> reviewPhotos = reviewPhotoService.findPhotos(review);
            List<Long> deletePhotoIds = List.of(reviewPhotos.get(0).getId());
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(deletePhotoIds);
            List<MultipartFile> updateImages = FileTestUtils.generateMultipartFiles(2);

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
            Member member = memberTestPersister.memberBuilder().save();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(Collections.emptyList());

            assertThatThrownBy(() -> reviewService.update(-1L, reviewUpdateRequest, null, member))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("일치하는 리뷰를 찾을 수 없습니다.");
        }

        @Test
        void 작성자가_아니면_예외가_발생한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();
            Member otherMember = memberTestPersister.memberBuilder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member).getId();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(Collections.emptyList());

            assertThatThrownBy(() -> reviewService.update(reviewId, reviewUpdateRequest, null, otherMember))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("본인이 작성한 리뷰가 아닙니다.");
        }

        @Test
        void 삭제하려는_사진이_리뷰의_사진이_아니면_예외가_발생한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member).getId();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(List.of(-1L));
            List<MultipartFile> updateImages = FileTestUtils.generateMultipartFiles(3);

            assertThatThrownBy(() -> reviewService.update(reviewId, reviewUpdateRequest, updateImages, member))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("삭제하려는 사진이 현재 리뷰에 존재하지 않습니다.");
        }

        @Test
        void 사진_개수가_최대_사진_개수를_초과하면_예외가_발생한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member).getId();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(Collections.emptyList());
            List<MultipartFile> updateImages = FileTestUtils.generateMultipartFiles(3);

            assertThatThrownBy(() -> reviewService.update(reviewId, reviewUpdateRequest, updateImages, member))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("리뷰에 사진은 최대 4장까지 가능합니다.");
        }
    }

    @Nested
    class 리뷰_삭제_시 {

        @Test
        void 정상적인_요청이라면_리뷰를_삭제한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member).getId();

            reviewService.delete(reviewId, member);

            assertThat(reviewRepository.findById(reviewId)).isEmpty();
        }

        @Test
        void 등록된_리뷰가_아니면_예외를_던진다() {
            Member member = memberTestPersister.memberBuilder().save();

            assertThatThrownBy(() -> reviewService.delete(-1L, member))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("일치하는 리뷰를 찾을 수 없습니다.");
        }

        @Test
        void 리뷰_작성자가_아니면_예외를_던진다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();
            Member otherMember = memberTestPersister.memberBuilder().save();
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
