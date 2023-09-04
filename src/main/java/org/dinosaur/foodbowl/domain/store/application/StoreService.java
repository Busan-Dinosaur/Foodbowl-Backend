package org.dinosaur.foodbowl.domain.store.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.application.dto.StoreCreateDto;
import org.dinosaur.foodbowl.domain.store.domain.Category;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.vo.Address;
import org.dinosaur.foodbowl.domain.store.domain.vo.CategoryType;
import org.dinosaur.foodbowl.domain.store.dto.response.CategoriesResponse;
import org.dinosaur.foodbowl.domain.store.exception.StoreExceptionType;
import org.dinosaur.foodbowl.domain.store.persistence.CategoryRepository;
import org.dinosaur.foodbowl.domain.store.persistence.StoreRepository;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final SchoolService schoolService;
    private final StoreSchoolService storeSchoolService;

    @Transactional(readOnly = true)
    public Store findById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(StoreExceptionType.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Optional<Store> findByLocationId(String locationId) {
        return storeRepository.findByLocationId(locationId);
    }

    @Transactional(readOnly = true)
    public CategoriesResponse getCategories() {
        List<Category> categories = categoryRepository.findAllByOrderById();
        return CategoriesResponse.from(categories);
    }

    @Transactional
    public Store create(StoreCreateDto storeCreateDto) {
        storeRepository.findByLocationId(storeCreateDto.locationId()).ifPresent(
                existingStore -> {
                    throw new BadRequestException(StoreExceptionType.DUPLICATE);
                }
        );
        Store store = storeRepository.save(convertToStore(storeCreateDto));
        if (storeCreateDto.schoolName() != null) {
            saveSchool(store, storeCreateDto.schoolName(), storeCreateDto.schoolX(), storeCreateDto.schoolY());
        }
        return store;
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
        School school = schoolService.findByName(schoolName)
                .orElseGet(() -> schoolService.save(schoolName, schoolX, schoolY));

        storeSchoolService.save(store, school);
    }
}
