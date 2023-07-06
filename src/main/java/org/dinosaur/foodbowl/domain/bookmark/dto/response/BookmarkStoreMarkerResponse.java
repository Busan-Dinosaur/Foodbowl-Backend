package org.dinosaur.foodbowl.domain.bookmark.dto.response;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.store.entity.Store;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkStoreMarkerResponse {

    private Long storeId;
    private String storeName;
    private String storeAddress;
    private BigDecimal x;
    private BigDecimal y;

    public static BookmarkStoreMarkerResponse from(Store store) {
        return new BookmarkStoreMarkerResponse(
                store.getId(),
                store.getStoreName(),
                store.getAddress().getAddressName(),
                store.getAddress().getX(),
                store.getAddress().getY()
        );
    }
}
