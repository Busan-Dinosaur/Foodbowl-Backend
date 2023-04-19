package org.dinosaur.foodbowl.domain.post.repository;

import org.dinosaur.foodbowl.domain.post.entity.Category;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface CategoryRepository extends Repository<Category, Long> {

    List<Category> findAllByOrderById();
}
