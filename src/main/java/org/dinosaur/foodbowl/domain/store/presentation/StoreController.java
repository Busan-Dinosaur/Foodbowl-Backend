package org.dinosaur.foodbowl.domain.store.presentation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.application.SchoolService;
import org.dinosaur.foodbowl.domain.store.application.StoreService;
import org.dinosaur.foodbowl.domain.store.dto.response.CategoriesResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.SchoolsResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreSearchResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/stores")
@RestController
public class StoreController implements StoreControllerDocs {

    private final StoreService storeService;
    private final SchoolService schoolService;

    @GetMapping("/search")
    public ResponseEntity<StoreSearchResponses> search(
            @RequestParam @NotBlank(message = "검색어는 빈 값이 될 수 없습니다.") String name,
            @RequestParam @NotNull(message = "경도는 반드시 포함되어야 합니다.") BigDecimal x,
            @RequestParam @NotNull(message = "위도는 반드시 포함되어야 합니다.") BigDecimal y,
            @RequestParam(defaultValue = "10") @Positive(message = "조회 크기는 1이상만 가능합니다.")
            @Max(value = 30, message = "최대 30개까지 조회가능합니다.") int size
    ) {
        StoreSearchResponses response = storeService.search(name, x, y, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<CategoriesResponse> getCategories() {
        CategoriesResponse response = storeService.getCategories();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/schools")
    public ResponseEntity<SchoolsResponse> getSchools() {
        SchoolsResponse response = schoolService.getSchools();
        return ResponseEntity.ok(response);
    }
}
