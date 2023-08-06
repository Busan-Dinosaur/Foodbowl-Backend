package org.dinosaur.foodbowl.domain.store.application;

import static org.dinosaur.foodbowl.domain.store.exception.StoreExceptionType.DUPLICATE_ERROR;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.domain.Category;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.StoreSchool;
import org.dinosaur.foodbowl.domain.store.domain.vo.Address;
import org.dinosaur.foodbowl.domain.store.domain.vo.CategoryType;
import org.dinosaur.foodbowl.domain.store.dto.StoreCreateDto;
import org.dinosaur.foodbowl.domain.store.persistence.CategoryRepository;
import org.dinosaur.foodbowl.domain.store.persistence.StoreRepository;
import org.dinosaur.foodbowl.domain.store.persistence.StoreSchoolRepository;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final StoreSchoolRepository storeSchoolRepository;
    private final SchoolService schoolService;

    @Transactional
    public Store create(StoreCreateDto storeCreateDto) {
        // TODO: 이 과정이 꼭 필요할까?
        storeRepository.findByAddress_AddressName(storeCreateDto.address()).ifPresent(
                existingStore -> {
                    throw new BadRequestException(DUPLICATE_ERROR);
                }
        );
        Store store = storeRepository.save(convertToStore(storeCreateDto));

        if (storeCreateDto.schoolName() != null) {
            String schoolName = storeCreateDto.schoolName();
            saveSchool(store, schoolName, storeCreateDto.schoolX(), storeCreateDto.schoolY());
        }
        return store;
    }

    // TODO: 리뷰(상위) 서비스에서 체크한 후 호출하는게 적절한가?
    public boolean checkIfStoreEmpty(String address) {
        return storeRepository.findByAddress_AddressName(address)
                .isEmpty();
    }

    private Store convertToStore(StoreCreateDto storeCreateDto) {
        CategoryType categoryType = CategoryType.of(storeCreateDto.category());
        Category category = categoryRepository.findById(categoryType.getId());
        Address address = Address.of(storeCreateDto.address(), storeCreateDto.storeX(), storeCreateDto.storeY());

        return Store.builder()
                .storeName(storeCreateDto.storeName())
                .category(category)
                .address(address)
                .storeUrl(storeCreateDto.storeUrl())
                .phone(storeCreateDto.phone())
                .build();
    }

    private void saveSchool(Store store, String schoolName, BigDecimal schoolX, BigDecimal schoolY) {
        if (schoolService.checkIfSchoolEmpty(schoolName)) {
            School school = schoolService.save(schoolName, schoolX, schoolY);
            storeSchoolRepository.save(StoreSchool.builder()
                    .store(store)
                    .school(school)
                    .build()
            );
        }
    }
}
