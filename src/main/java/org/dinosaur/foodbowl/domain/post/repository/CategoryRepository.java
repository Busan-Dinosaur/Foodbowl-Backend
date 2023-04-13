package org.dinosaur.foodbowl.domain.post.repository;

import java.util.List;
import org.dinosaur.foodbowl.domain.post.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface CategoryRepository extends Repository<Category, Long> {

    @Query(value = "select * from Category ORDER BY id ASC", nativeQuery = true)
    List<Category> findAll();
}
