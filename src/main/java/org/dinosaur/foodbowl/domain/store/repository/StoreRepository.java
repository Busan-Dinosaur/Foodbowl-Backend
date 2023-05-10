package org.dinosaur.foodbowl.domain.store.repository;

import org.dinosaur.foodbowl.domain.store.entity.Store;
import org.springframework.data.repository.Repository;

public interface StoreRepository extends Repository<Store, Long> {

    Store save(Store store);
}
