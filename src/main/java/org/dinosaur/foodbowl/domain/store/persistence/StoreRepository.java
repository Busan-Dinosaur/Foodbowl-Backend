package org.dinosaur.foodbowl.domain.store.persistence;

import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.springframework.data.repository.Repository;

public interface StoreRepository extends Repository<Store, Long> {
}
