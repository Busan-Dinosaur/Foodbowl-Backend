package org.dinosaur.foodbowl.domain.store.persistence;

import java.util.List;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreSearchQueryResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface StoreRepository extends Repository<Store, Long> {

    Optional<Store> findByLocationId(String locationId);

    Optional<Store> findById(Long id);

    @Query(
            nativeQuery = true,
            value = "SELECT "
                    + "s.id as storeId, "
                    + "s.store_name as storeName, " +
                    "ST_Distance_Sphere(ST_PointFromText(CONCAT('POINT(', :y, ' ', :x, ')'), 4326), s.coordinate) as distance "
                    +
                    "FROM Store s " +
                    "WHERE s.store_name LIKE CONCAT('%', :name, '%') " +
                    "ORDER BY ST_Distance_Sphere(ST_PointFromText(CONCAT('POINT(', :y, ' ', :x, ')'), 4326), s.coordinate) ASC " +
                    "LIMIT :size OFFSET 0"
    )
    List<StoreSearchQueryResponse> search(
            @Param("name") String name,
            @Param("x") double x,
            @Param("y") double y,
            @Param("size") int size);

    Store save(Store store);
}

