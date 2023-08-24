package org.dinosaur.foodbowl.global.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Slice;

@Schema(description = "페이지 응답")
public record PageResponse<T>(
        @Schema(description = "페이지 응답 본문 목록")
        List<T> content,

        @Schema(description = "첫 페이지 여부", example = "true")
        boolean isFirst,

        @Schema(description = "마지막 페이지 여부", example = "false")
        boolean isLast,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext,

        @Schema(description = "현재 페이지", example = "0")
        int currentPage,

        @Schema(description = "현재 페이지 크기", example = "20")
        int currentSize
) {

    public static <T> PageResponse<T> from(Slice<T> slice) {
        return new PageResponse<>(
                slice.getContent(),
                slice.isFirst(),
                slice.isLast(),
                slice.hasNext(),
                slice.getNumber(),
                slice.getSize()
        );
    }
}
