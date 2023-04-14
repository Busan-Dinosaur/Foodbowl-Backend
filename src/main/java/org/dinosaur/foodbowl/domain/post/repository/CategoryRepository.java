package org.dinosaur.foodbowl.domain.post.repository;

import java.util.List;
import org.dinosaur.foodbowl.domain.post.entity.Category;
import org.springframework.data.repository.Repository;

public interface CategoryRepository extends Repository<Category, Long> {

    List<Category> findAllByOrderById();
}
