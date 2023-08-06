package org.dinosaur.foodbowl.domain.store.persistence;

import org.dinosaur.foodbowl.domain.store.domain.Category;
import org.springframework.data.repository.Repository;

public interface CategoryRepository extends Repository<Category, Long> {

    Category findById(Long id);
}
