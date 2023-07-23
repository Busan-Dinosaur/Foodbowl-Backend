package org.dinosaur.foodbowl.domain.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreRequest {

    @NotBlank(message = "가게 이름은 반드시 입력되어야 합니다.")
    @Size(max = 100, message = "가게 이름의 길이는 {max}까지 가능합니다.")
    private String storeName;

    @NotBlank(message = "주소는 반드시 입력되어야 합니다.")
    @Size(max = 512, message = "주소의 길이는 {max}까지 가능합니다.")
    private String addressName;

    @NotBlank(message = "광역시 또는 도는 반드시 입력되어야 합니다.")
    @Size(max = 45, message = "광역시 또는 도의 길이는 {max}까지 가능합니다.")
    private String region1depthName;

    @NotBlank(message = "시/군/구는 반드시 입력되어야 합니다.")
    @Size(max = 45, message = "시/군/구의 길이는 {max}까지 가능합니다.")
    private String region2depthName;

    @NotBlank(message = "읍/면/동은 반드시 입력되어야 합니다.")
    @Size(max = 45, message = "읍/면/동의 길이는 {max}까지 가능합니다.")
    private String region3depthName;

    private String roadName;

    @NotBlank(message = "가게 정보 url은 반드시 입력되어야 합니다.")
    private String storeUrl;

    private String phone;

    @NotNull(message = "경도는 반드시 입력되어야 합니다.")
    private BigDecimal x;

    @NotNull(message = "위도는 반드시 입력되어야 합니다.")
    private BigDecimal y;
}
