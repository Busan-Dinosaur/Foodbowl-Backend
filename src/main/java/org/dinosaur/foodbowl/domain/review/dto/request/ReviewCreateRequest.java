package org.dinosaur.foodbowl.domain.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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

    @Size(max = 4)
    private List<MultipartFile> images;

    private String schoolName;
    private BigDecimal schoolX;
    private BigDecimal schoolY;
}
