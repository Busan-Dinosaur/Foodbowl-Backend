package org.dinosaur.foodbowl.domain.store.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
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
    private String region1DepthName;

    @NotNull
    @Column(name = "region_2depth_name", length = 45)
    private String region2DepthName;

    @NotNull
    @Column(name = "region_3depth_name", length = 45)
    private String region3DepthName;

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

    @NotNull
    @Column(name = "x")
    private Double longitude;

    @NotNull
    @Column(name = "y")
    private Double latitude;

    @Builder
    private Address(String addressName,
                    String region1DepthName,
                    String region2DepthName,
                    String region3DepthName,
                    String roadName,
                    String undergroundYN,
                    String mainBuildingNo,
                    String subBuildingNo,
                    String buildingName,
                    Double longitude,
                    Double latitude) {
        this.addressName = addressName;
        this.region1DepthName = region1DepthName;
        this.region2DepthName = region2DepthName;
        this.region3DepthName = region3DepthName;
        this.mainBuildingNo = mainBuildingNo;
        this.roadName = roadName;
        this.undergroundYN = undergroundYN;
        this.subBuildingNo = subBuildingNo;
        this.buildingName = buildingName;
        this.longitude = longitude;
        this.latitude = latitude;
    }

}
