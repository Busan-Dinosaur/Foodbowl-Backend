package org.dinosaur.foodbowl.domain.store.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.dinosaur.foodbowl.domain.store.domain.Category;
import org.dinosaur.foodbowl.domain.store.domain.vo.CategoryType;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class CategoryRepositoryTest extends PersistenceTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void ID를_기준으로_카테고리_목록을_오름차순으로_정렬하여_조회한다() {
        List<Category> categories = categoryRepository.findAllByOrderById();
        CategoryType[] categoryTypes = categories.stream()
                .map(Category::getCategoryType)
                .toArray(CategoryType[]::new);

        assertThat(CategoryType.values()).containsExactly(categoryTypes);
    }
}
