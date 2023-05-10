package org.dinosaur.foodbowl.domain.store.repository;

import java.util.List;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.store.entity.Store;
import org.springframework.data.repository.Repository;

public interface StoreRepository extends Repository<Store, Long> {

    Optional<Store> findById(Long id);

    Optional<Store> findByStoreName(String storeName);

    List<Store> findAll();

    Store save(Store store);
}
