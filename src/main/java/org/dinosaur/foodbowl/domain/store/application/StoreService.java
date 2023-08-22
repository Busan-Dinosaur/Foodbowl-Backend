package org.dinosaur.foodbowl.domain.store.application;

import static org.dinosaur.foodbowl.domain.store.exception.StoreExceptionType.DUPLICATE_ERROR;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.domain.Category;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.StoreSchool;
import org.dinosaur.foodbowl.domain.store.domain.vo.Address;
import org.dinosaur.foodbowl.domain.store.domain.vo.CategoryType;
import org.dinosaur.foodbowl.domain.store.application.dto.StoreCreateDto;
import org.dinosaur.foodbowl.domain.store.dto.response.CategoryResponses;
import org.dinosaur.foodbowl.domain.store.persistence.CategoryRepository;
import org.dinosaur.foodbowl.domain.store.persistence.StoreRepository;
import org.dinosaur.foodbowl.domain.store.persistence.StoreSchoolRepository;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final StoreSchoolRepository storeSchoolRepository;
    private final SchoolService schoolService;

    @Transactional(readOnly = true)
    public CategoryResponses getCategories() {
        List<Category> categories = categoryRepository.findAllByOrderById();
        return CategoryResponses.from(categories);
    }

    @Transactional
    public Store create(StoreCreateDto storeCreateDto) {
        storeRepository.findByLocationId(storeCreateDto.locationId()).ifPresent(
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

    @Transactional(readOnly = true)
    public Optional<Store> findByLocationId(String locationId) {
        return storeRepository.findByLocationId(locationId);
    }

    private Store convertToStore(StoreCreateDto storeCreateDto) {
        CategoryType categoryType = CategoryType.of(storeCreateDto.category());
        Category category = categoryRepository.findById(categoryType.getId());
        Address address = Address.of(storeCreateDto.address(), storeCreateDto.storeX(), storeCreateDto.storeY());

        return Store.builder()
                .locationId(storeCreateDto.locationId())
                .storeName(storeCreateDto.storeName())
                .category(category)
                .address(address)
                .storeUrl(storeCreateDto.storeUrl())
                .phone(storeCreateDto.phone())
                .build();
    }

    private void saveSchool(Store store, String schoolName, BigDecimal schoolX, BigDecimal schoolY) {
        if (schoolService.findByName(schoolName).isEmpty()) {
            School school = schoolService.save(schoolName, schoolX, schoolY);
            storeSchoolRepository.save(StoreSchool.builder()
                    .store(store)
                    .school(school)
                    .build()
            );
        }
    }
}
