package org.dinosaur.foodbowl.domain.store.application;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.dto.StoreRequest;
import org.dinosaur.foodbowl.domain.store.dto.StoreResponse;
import org.dinosaur.foodbowl.domain.store.entity.Store;
import org.dinosaur.foodbowl.domain.store.repository.StoreRepository;
import org.dinosaur.foodbowl.global.exception.ErrorStatus;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreResponse findOne(Long memberId) {
        Store store = storeRepository.findById(memberId)
                .orElseThrow(() -> new FoodbowlException(ErrorStatus.STORE_NOT_FOUND));

        return StoreResponse.from(store);
    }

    public List<StoreResponse> findAll() {
        return storeRepository.findAll().stream()
                .map(StoreResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public StoreResponse save(StoreRequest storeRequest) {
        if (storeRepository.findByStoreName(storeRequest.getStoreName()).isPresent()) {
            throw new FoodbowlException(ErrorStatus.STORE_DUPLICATED);
        }

        Store savedStore = storeRepository.save(createStore(storeRequest));
        return StoreResponse.from(savedStore);
    }

    private Store createStore(StoreRequest storeRequest) {
        return Store.builder()
                .storeName(storeRequest.getStoreName())
                .address(storeRequest.toAddress())
                .build();
    }
}
