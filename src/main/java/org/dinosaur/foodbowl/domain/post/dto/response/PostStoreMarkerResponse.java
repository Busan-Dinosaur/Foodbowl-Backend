package org.dinosaur.foodbowl.domain.post.dto.response;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.store.entity.Store;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostStoreMarkerResponse {

    private Long storeId;
    private String storeName;
    private String storeAddress;
    private BigDecimal x;
    private BigDecimal y;

    public static PostStoreMarkerResponse from(Store store) {
        return new PostStoreMarkerResponse(
                store.getId(),
                store.getStoreName(),
                store.getAddress().getAddressName(),
                store.getAddress().getX(),
                store.getAddress().getY()
        );
    }
}
