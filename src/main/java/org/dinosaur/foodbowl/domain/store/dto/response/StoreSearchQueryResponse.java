package org.dinosaur.foodbowl.domain.store.dto.response;

public interface StoreSearchQueryResponse {

    Long getStoreId();

    String getStoreName();

    double getDistance();

    long getReviewCount();
}
