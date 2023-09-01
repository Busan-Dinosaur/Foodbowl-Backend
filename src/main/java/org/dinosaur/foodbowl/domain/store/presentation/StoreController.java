package org.dinosaur.foodbowl.domain.store.presentation;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.application.StoreService;
import org.dinosaur.foodbowl.domain.store.dto.response.CategoriesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/stores")
@RestController
public class StoreController implements StoreControllerDocs {

    private final StoreService storeService;

    @GetMapping("/categories")
    public ResponseEntity<CategoriesResponse> getCategories() {
        CategoriesResponse response = storeService.getCategories();
        return ResponseEntity.ok(response);
    }
}
