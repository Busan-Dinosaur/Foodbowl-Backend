package org.dinosaur.foodbowl.domain.store.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.review.application.dto.MapCoordinateBoundDto;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.persistence.StoreCustomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StoreCustomService {

    private final StoreCustomRepository storeCustomRepository;

    @Transactional(readOnly = true)
    public List<Store> getStoresByFollowingInMapBounds(Long memberId, MapCoordinateBoundDto mapCoordinateBoundDto) {
        return storeCustomRepository.findStoresByFollowingInMapBounds(memberId, mapCoordinateBoundDto);
    }
}
