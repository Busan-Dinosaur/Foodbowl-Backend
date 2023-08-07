package org.dinosaur.foodbowl.domain.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.dinosaur.foodbowl.domain.store.domain.Category;

@Schema(description = "카테고리 목록 응답")
public record CategoryResponses(
        @Schema(
                description = "카테고리 응답 목록",
                example = "[{\"id\": 1, \"name\": \"카페\"}, {\"id\": 2, \"name\": \"술집\"}]"
        )
        List<CategoryResponse> categories
) {

    public static CategoryResponses from(List<Category> categories) {
        return new CategoryResponses(listOf(categories));
    }

    private static List<CategoryResponse> listOf(List<Category> categories) {
        return categories.stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
