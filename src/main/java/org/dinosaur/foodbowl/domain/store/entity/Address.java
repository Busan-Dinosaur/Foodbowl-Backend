package org.dinosaur.foodbowl.domain.store.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Address {

    @NotNull
    @Column(name = "address_name", length = 512)
    private String addressName;

    @NotNull
    @Column(name = "region_1depth_name", length = 45)
    private String region1depthName;

    @NotNull
    @Column(name = "region_2depth_name", length = 45)
    private String region2depthName;

    @NotNull
    @Column(name = "region_3depth_name", length = 45)
    private String region3depthName;

    @Column(name = "road_name", length = 45)
    private String roadName;

    @Column(name = "underground_yn", length = 45)
    private String undergroundYN;

    @Column(name = "main_building_no", length = 45)
    private String mainBuildingNo;

    @Column(name = "sub_building_no", length = 45)
    private String subBuildingNo;

    @Column(name = "building_name", length = 45)
    private String buildingName;

    @Column(name = "zone_no", length = 45)
    private String zoneNo;

    @NotNull
    @Column(name = "x")
    private BigDecimal x;

    @NotNull
    @Column(name = "y")
    private BigDecimal y;

    @Builder
    private Address(String addressName,
                    String region1depthName,
                    String region2depthName,
                    String region3depthName,
                    String roadName,
                    String undergroundYN,
                    String mainBuildingNo,
                    String subBuildingNo,
                    String buildingName,
                    String zoneNo,
                    BigDecimal x,
                    BigDecimal y) {
        this.addressName = addressName;
        this.region1depthName = region1depthName;
        this.region2depthName = region2depthName;
        this.region3depthName = region3depthName;
        this.mainBuildingNo = mainBuildingNo;
        this.roadName = roadName;
        this.undergroundYN = undergroundYN;
        this.subBuildingNo = subBuildingNo;
        this.buildingName = buildingName;
        this.zoneNo = zoneNo;
        this.x = x;
        this.y = y;
    }
}
