package org.dinosaur.foodbowl.domain.store.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.dto.StoreResponse;
import org.dinosaur.foodbowl.domain.store.entity.Store;
import org.dinosaur.foodbowl.domain.store.repository.StoreRepository;
import org.dinosaur.foodbowl.global.exception.ErrorStatus;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreResponse findOne(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new FoodbowlException(ErrorStatus.STORE_NOT_FOUND));

        return StoreResponse.from(store);
    }

    public List<StoreResponse> findAll() {
        List<Store> stores = storeRepository.findAll();
        return stores.stream()
                .map(StoreResponse::from)
                .collect(Collectors.toList());
    }
}
