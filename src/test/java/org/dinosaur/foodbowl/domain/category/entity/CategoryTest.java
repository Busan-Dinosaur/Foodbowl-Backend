package org.dinosaur.foodbowl.domain.category.entity;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.post.entity.Category;
import org.dinosaur.foodbowl.domain.post.entity.Category.CategoryType;
import org.dinosaur.foodbowl.domain.post.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest extends RepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("DB에 저장된 카테고리와 일치한다.")
    void hasSameCategoryWithDB() {
        List<Category> categories = Arrays.stream(CategoryType.values())
                .map(Category::from)
                .collect(Collectors.toList());

        List<Category> dbCategories = categoryRepository.findAllByOrderById();

        assertThat(dbCategories).isEqualTo(categories);
    }
}
