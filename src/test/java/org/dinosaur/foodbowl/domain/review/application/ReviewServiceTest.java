package org.dinosaur.foodbowl.domain.review.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewRepository;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
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
    private ReviewRepository reviewRepository;

    @Nested
    class 리뷰_저장_시 {

        @Test
        void 사진_없이_저장한다() {
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();

            Long reviewId = reviewService.create(reviewCreateRequest, null, member);

            assertThat(reviewId).isNotNull();
        }

        @Test
        void 사진을_포함해서_저장한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();

            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member);

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

            Long savedReviewId = reviewService.create(otherReviewCreateRequest, null, otherMember);

            assertThat(savedReviewId).isNotNull();
        }
    }

    @Nested
    class 리뷰_삭제_시 {

        @Test
        void 정상적으로_삭제한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member);

            reviewService.delete(reviewId, member);

            assertThat(reviewRepository.findById(reviewId)).isEmpty();
        }

        @Test
        void 존재하는_리뷰가_아니면_예외가_발생한다() {
            Member member = memberTestPersister.memberBuilder().save();

            assertThatThrownBy(() -> reviewService.delete(-1L, member))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("일치하는 리뷰를 찾을 수 없습니다.");
        }

        @Test
        void 작성자가_아니면_예외가_발생한다() {
            List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            Member member = memberTestPersister.memberBuilder().save();
            Member otherMember = memberTestPersister.memberBuilder().save();
            Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member);

            assertThatThrownBy(() -> reviewService.delete(reviewId, otherMember))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("본인이 작성한 리뷰만 삭제할 수 있습니다.");
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
                BigDecimal.valueOf(124.1234),
                BigDecimal.valueOf(34.545)
        );
    }
}
