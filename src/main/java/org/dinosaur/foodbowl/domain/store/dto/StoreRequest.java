package org.dinosaur.foodbowl.domain.store.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreRequest {

    @NotBlank(message = "가게 이름은 반드시 입력되어야 합니다.")
    private String storeName;

    @NotBlank(message = "주소는 반드시 입력되어야 합니다.")
    private String addressName;

    @NotBlank(message = "광역시 또는 도는 반드시 입력되어야 합니다.")
    private String region1depthName;

    @NotBlank(message = "시/군/구는 반드시 입력되어야 합니다.")
    private String region2depthName;

    @NotBlank(message = "읍/면/동은 반드시 입력되어야 합니다.")
    private String region3depthName;

    private String roadName;

    private String undergroundYN;

    private String mainBuildingNo;

    private String subBuildingNo;

    private String buildingName;

    private String zoneNo;

    @NotNull(message = "경도는 반드시 입력되어야 합니다.")
    @DecimalMin(value = "-180", message = "경도의 최소값은 -180입니다.")
    @DecimalMax(value = "180", message = "경도의 최대값은 180입니다.")
    private BigDecimal x;

    @NotNull(message = "위도는 반드시 입력되어야 합니다.")
    @DecimalMin(value = "-90", message = "위도의 최소값은 -90입니다.")
    @DecimalMax(value = "90", message = "위도의 최대값은 90입니다.")
    private BigDecimal y;
}
