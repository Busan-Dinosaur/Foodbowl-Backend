package org.dinosaur.foodbowl.domain.store.persistence;

import java.util.List;
import org.dinosaur.foodbowl.domain.store.domain.Category;
import org.springframework.data.repository.Repository;

public interface CategoryRepository extends Repository<Category, Long> {

    List<Category> findAllByOrderById();
}
