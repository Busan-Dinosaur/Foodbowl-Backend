package org.dinosaur.foodbowl.domain.review.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.file.FileTestUtils;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("NonAsciiCharacters")
class ReviewServiceTest extends IntegrationTest {

    @Autowired
    private ReviewService reviewService;

    @Test
    void 사진_없이_리뷰를_저장한다() {
        ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
        Member member = memberTestPersister.memberBuilder().save();

        Long reviewId = reviewService.create(reviewCreateRequest, null, member);

        assertThat(reviewId).isNotNull();
    }

    @Test
    void 사진을_포함해서_리뷰를_저장한다() {
        List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(2);
        ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
        Member member = memberTestPersister.memberBuilder().save();

        Long reviewId = reviewService.create(reviewCreateRequest, multipartFiles, member);

        assertThat(reviewId).isNotNull();
        FileTestUtils.cleanUp();
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
