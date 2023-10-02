package org.dinosaur.foodbowl.domain.review.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import org.dinosaur.foodbowl.domain.review.domain.Review;

@Schema(description = "리뷰 본문 응답")
public record ReviewContentResponse(
        @Schema(description = "리뷰 ID", example = "1")
        Long id,

        @Schema(description = "리뷰 내용", example = "정말 맛있어요!")
        String content,

        @Schema(description = "리뷰 이미지 경로 목록", example = "['imageA.png', 'imageB.png']")
        List<String> imagePaths,

        @Schema(description = "리뷰 생성시간", example = "2023-10-01 23:59:59")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @Schema(description = "리뷰 수정시간", example = "2023-10-01 23:59:59")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {

    public static ReviewContentResponse of(Review review, List<String> imagePaths) {
        return new ReviewContentResponse(
                review.getId(),
                review.getContent(),
                imagePaths,
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
