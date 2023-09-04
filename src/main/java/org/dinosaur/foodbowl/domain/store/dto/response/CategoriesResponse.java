package org.dinosaur.foodbowl.domain.store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.dinosaur.foodbowl.domain.store.domain.Category;

@Schema(description = "카테고리 목록 응답")
public record CategoriesResponse(
        @Schema(description = "카테고리 응답 목록")
        List<CategoryResponse> categories
) {

    public static CategoriesResponse from(List<Category> categories) {
        return new CategoriesResponse(convertToResponses(categories));
    }

    private static List<CategoryResponse> convertToResponses(List<Category> categories) {
        return categories.stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
