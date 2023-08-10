package org.dinosaur.foodbowl.domain.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewCreateRequest {

    @NotBlank(message = "가게 이름은 반드시 포함되어야 합니다.")
    private String storeName;

    @NotBlank(message = "가게 주소는 반드시 포함되어야 합니다.")
    private String storeAddress;

    @NotNull(message = "가게 경도는 반드시 포함되어야 합니다.")
    private BigDecimal x;

    @NotNull(message = "가게 위도는 반드시 포함되어야 합니다.")
    private BigDecimal y;

    @NotBlank(message = "카카오 서버의 가게 정보 url 반드시 포함되어야 합니다.")
    private String storeUrl;

    private String phone;

    @NotBlank(message = "가게 카테고리는 반드시 포함되어야 합니다.")
    private String category;

    @NotBlank(message = "가게 리뷰는 반드시 포함되어야 합니다.")
    private String reviewContent;

    private String schoolName;
    private BigDecimal schoolX;
    private BigDecimal schoolY;
}
