package org.dinosaur.foodbowl.domain.store.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.dto.StoreRequest;
import org.dinosaur.foodbowl.domain.store.dto.StoreResponse;
import org.dinosaur.foodbowl.domain.store.entity.Address;
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

    @Transactional
    public StoreResponse save(StoreRequest storeRequest) {
        if (storeRepository.findByStoreName(storeRequest.getStoreName()).isPresent()) {
            throw new FoodbowlException(ErrorStatus.STORE_DUPLICATED);
        }

        Address address = Address.builder()
                .addressName(storeRequest.getAddressName())
                .region1depthName(storeRequest.getRegion1depthName())
                .region2depthName(storeRequest.getRegion2depthName())
                .region3depthName(storeRequest.getRegion3depthName())
                .roadName(storeRequest.getRoadName())
                .undergroundYN(storeRequest.getUndergroundYN())
                .mainBuildingNo(storeRequest.getMainBuildingNo())
                .subBuildingNo(storeRequest.getSubBuildingNo())
                .buildingName(storeRequest.getBuildingName())
                .zoneNo(storeRequest.getZoneNo())
                .x(storeRequest.getX())
                .y(storeRequest.getY())
                .build();

        Store store = Store.builder()
                .storeName(storeRequest.getStoreName())
                .address(address)
                .build();
        storeRepository.save(store);
        return StoreResponse.from(store);
    }
}
