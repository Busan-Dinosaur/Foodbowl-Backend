package org.dinosaur.foodbowl.domain.store.dto;

import java.math.BigDecimal;

public record StoreCreateDto(String storeName,
                             String category,
                             String address,
                             BigDecimal storeX,
                             BigDecimal storeY,
                             String storeUrl,
                             String phone,
                             String schoolName,
                             BigDecimal schoolX,
                             BigDecimal schoolY) {
}
