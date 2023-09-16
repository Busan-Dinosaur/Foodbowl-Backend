package org.dinosaur.foodbowl.domain.review.application.dto.request;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.dinosaur.foodbowl.global.presentation.PositiveList;

@Schema(description = "리뷰 수정 요청")
public record ReviewUpdateRequest(
        @Schema(description = "리뷰 수정 내용", example = "가성비로 먹기 좋아요.", requiredMode = REQUIRED)
        @NotBlank(message = "수정할 리뷰 내용은 반드시 포함되어야 합니다.")
        String reviewContent,

        @Schema(
                description = "리뷰 수정 시 삭제하는 사진 ID 배열. 삭제하는 사진이 없는 경우 빈 배열([]).",
                example = "[1,2,3] or []",
                requiredMode = REQUIRED
        )
        @NotNull(message = "삭제하는 사진 배열은 반드시 포함되어야 합니다.")
        @PositiveList(message = "삭제하는 사진 ID는 양수만 가능합니다.")
        List<Long> deletePhotoIds
) {
}
