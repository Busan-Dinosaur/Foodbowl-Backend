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

    @Column(name = "road_name", length = 100)
    private String roadName;

    @NotNull
    @Column(name = "x")
    private BigDecimal x;

    @NotNull
    @Column(name = "y")
    private BigDecimal y;

    @Builder
    private Address(
            String addressName, String region1depthName, String region2depthName, String region3depthName,
            String roadName, BigDecimal x, BigDecimal y
    ) {
        this.addressName = addressName;
        this.region1depthName = region1depthName;
        this.region2depthName = region2depthName;
        this.region3depthName = region3depthName;
        this.roadName = roadName;
        this.x = x;
        this.y = y;
    }
}
