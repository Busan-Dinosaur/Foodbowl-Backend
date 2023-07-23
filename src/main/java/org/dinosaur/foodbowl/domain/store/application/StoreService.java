package org.dinosaur.foodbowl.domain.store.application;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.dto.StoreRequest;
import org.dinosaur.foodbowl.domain.store.dto.StoreResponse;
import org.dinosaur.foodbowl.domain.store.entity.Address;
import org.dinosaur.foodbowl.domain.store.entity.Store;
import org.dinosaur.foodbowl.domain.store.repository.StoreRepository;
import org.dinosaur.foodbowl.exception.ErrorStatus;
import org.dinosaur.foodbowl.exception.FoodbowlException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreResponse findOne(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new FoodbowlException(ErrorStatus.STORE_NOT_FOUND));

        return StoreResponse.from(store);
    }

    public StoreResponse findByAddress(String address) {
        Store store = storeRepository.findByAddress_AddressName(address)
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
        storeRepository.findByStoreName(storeRequest.getStoreName()).ifPresent(
                store -> {
                    throw new FoodbowlException(ErrorStatus.STORE_DUPLICATED);
                }
        );

        Store savedStore = storeRepository.save(createStore(storeRequest));
        return StoreResponse.from(savedStore);
    }

    private Store createStore(StoreRequest storeRequest) {
        return Store.builder()
                .storeName(storeRequest.getStoreName())
                .address(convertToAddress(storeRequest))
                .build();
    }

    private Address convertToAddress(StoreRequest storeRequest) {
        return Address.builder()
                .addressName(storeRequest.getAddressName())
                .region1depthName(storeRequest.getRegion1depthName())
                .region2depthName(storeRequest.getRegion2depthName())
                .region3depthName(storeRequest.getRegion3depthName())
                .roadName(storeRequest.getRoadName())
                .x(storeRequest.getX())
                .y(storeRequest.getY())
                .build();
    }
}
