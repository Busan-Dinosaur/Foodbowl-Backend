package org.dinosaur.foodbowl.domain.store.application.dto;

import java.math.BigDecimal;

public record StoreCreateDto(
        String locationId,
        String storeName,
        String category,
        String address,
        BigDecimal storeX,
        BigDecimal storeY,
        String storeUrl,
        String phone,
        String schoolName,
        String schoolAddress,
        BigDecimal schoolX,
        BigDecimal schoolY
) {
}
