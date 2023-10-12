package org.dinosaur.foodbowl.domain.store.persistence;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

public interface StoreRepository extends Repository<Store, Long> {

    Optional<Store> findByLocationId(String locationId);

    @EntityGraph(attributePaths = {"category"})
    Optional<Store> findById(Long id);

    Store save(Store store);
}

