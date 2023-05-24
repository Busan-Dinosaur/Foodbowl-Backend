package org.dinosaur.foodbowl.domain.store.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.application.StoreService;
import org.dinosaur.foodbowl.domain.store.dto.StoreRequest;
import org.dinosaur.foodbowl.domain.store.dto.StoreResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
@RestController
public class StoreController {

    private static final String ADDRESS_REQUEST_DELIMITER = "+";
    private static final String ADDRESS_DELIMITER = " ";

    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<StoreResponse> findStore(
            @RequestParam @NotBlank(message = "주소는 반드시 포함되어야 합니다.") String address) {
        StoreResponse storeResponse = storeService.findByAddress(address.replace(ADDRESS_REQUEST_DELIMITER, ADDRESS_DELIMITER));
        return ResponseEntity.ok(storeResponse);
    }

    @PostMapping
    public ResponseEntity<StoreResponse> saveStore(@Valid @RequestBody StoreRequest storeRequest) {
        StoreResponse storeResponse = storeService.save(storeRequest);
        return ResponseEntity.ok(storeResponse);
    }
}
