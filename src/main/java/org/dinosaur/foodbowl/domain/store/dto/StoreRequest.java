package org.dinosaur.foodbowl.domain.store.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.store.entity.Address;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StoreRequest {

    @NotBlank(message = "가게 이름은 반드시 입력되어야 합니다.")
    @Length(max = 100, message = "길이는 {max}까지 가능합니다.")
    private String storeName;

    @NotBlank(message = "주소는 반드시 입력되어야 합니다.")
    @Length(max = 512, message = "길이는 {max}까지 가능합니다.")
    private String addressName;

    @NotBlank(message = "광역시 또는 도는 반드시 입력되어야 합니다.")
    @Length(max = 45, message = "길이는 {max}까지 가능합니다.")
    private String region1depthName;

    @NotBlank(message = "시/군/구는 반드시 입력되어야 합니다.")
    @Length(max = 45, message = "길이는 {max}까지 가능합니다.")
    private String region2depthName;

    @NotBlank(message = "읍/면/동은 반드시 입력되어야 합니다.")
    @Length(max = 45, message = "길이는 {max}까지 가능합니다.")
    private String region3depthName;

    private String roadName;

    @Pattern(regexp = "^[YN]$")
    private String undergroundYN;

    private String mainBuildingNo;

    private String subBuildingNo;

    private String buildingName;

    private String zoneNo;

    @NotNull(message = "경도는 반드시 입력되어야 합니다.")
    @DecimalMin(value = "-180", message = "경도의 최소값은 {value}입니다.")
    @DecimalMax(value = "180", message = "경도의 최대값은 {value}입니다.")
    private BigDecimal x;

    @NotNull(message = "위도는 반드시 입력되어야 합니다.")
    @DecimalMin(value = "-90", message = "위도의 최소값은 {value}입니다.")
    @DecimalMax(value = "90", message = "위도의 최대값은 {value}입니다.")
    private BigDecimal y;

    public Address toAddress() {
        return Address.builder()
                .addressName(this.addressName)
                .region1depthName(this.region1depthName)
                .region2depthName(this.region2depthName)
                .region3depthName(this.region3depthName)
                .roadName(this.roadName)
                .undergroundYN(this.undergroundYN)
                .mainBuildingNo(this.mainBuildingNo)
                .subBuildingNo(this.subBuildingNo)
                .buildingName(this.buildingName)
                .zoneNo(this.zoneNo)
                .x(this.x)
                .y(this.y)
                .build();
    }
}
