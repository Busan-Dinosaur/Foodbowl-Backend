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

    @NotBlank
    private String storeName;

    @NotBlank
    private String storeAddress;

    @NotNull
    private BigDecimal x;

    @NotNull
    private BigDecimal y;

    @NotBlank
    private String storeUrl;

    private String phone;

    @NotBlank
    private String category;

    @NotBlank
    private String reviewContent;

    private String schoolName;
    private BigDecimal schoolX;
    private BigDecimal schoolY;
}
