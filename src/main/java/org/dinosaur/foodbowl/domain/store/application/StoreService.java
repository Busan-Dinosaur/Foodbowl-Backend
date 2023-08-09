package org.dinosaur.foodbowl.domain.store.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.domain.Category;
import org.dinosaur.foodbowl.domain.store.dto.response.CategoryResponses;
import org.dinosaur.foodbowl.domain.store.persistence.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StoreService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public CategoryResponses getCategories() {
        List<Category> categories = categoryRepository.findAllByOrderById();
        return CategoryResponses.from(categories);
    }
}
