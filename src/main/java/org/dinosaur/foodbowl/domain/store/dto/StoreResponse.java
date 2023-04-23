package org.dinosaur.foodbowl.domain.store.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dinosaur.foodbowl.domain.store.entity.Address;
import org.dinosaur.foodbowl.domain.store.entity.Store;

@Getter
@AllArgsConstructor
public class StoreResponse {

    private Long id;

    private String storeName;

    private String addressName;

    private String region1depthName;

    private String region2depthName;

    private String region3depthName;

    private String roadName;

    private String undergroundYN;

    private String mainBuildingNo;

    private String subBuildingNo;

    private String buildingName;

    private String zoneNo;

    private BigDecimal x;

    private BigDecimal y;

    public static StoreResponse from(Store store) {
        Address address = store.getAddress();
        return new StoreResponse(
                store.getId(),
                store.getStoreName(),
                address.getAddressName(),
                address.getRegion1depthName(),
                address.getRegion2depthName(),
                address.getRegion3depthName(),
                address.getRoadName(),
                address.getUndergroundYN(),
                address.getMainBuildingNo(),
                address.getSubBuildingNo(),
                address.getBuildingName(),
                address.getZoneNo(),
                address.getX(),
                address.getY());
    }
}
