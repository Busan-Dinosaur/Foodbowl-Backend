package org.dinosaur.foodbowl.domain.store.domain.vo;

import static org.dinosaur.foodbowl.domain.store.exception.StoreExceptionType.INVALID_ADDRESS;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    private static final String DELIMITER = " ";
    private static final int MIN_SIZE = 4;

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

    @NotNull
    @Column(name = "road_name", length = 100)
    private String roadName;

    @Valid
    @Embedded
    private Coordinate coordinate;

    public static Address of(String storeAddress, BigDecimal x, BigDecimal y) {
        List<String> addressElements = Arrays.stream(storeAddress.split(DELIMITER)).toList();

        if (addressElements.size() < MIN_SIZE) {
            throw new InvalidArgumentException(INVALID_ADDRESS);
        }
        String roadName = String.join(DELIMITER, addressElements.subList(3, addressElements.size()));
        return Address.builder()
                .addressName(storeAddress)
                .region1depthName(addressElements.get(0))
                .region2depthName(addressElements.get(1))
                .region3depthName(addressElements.get(2))
                .roadName(roadName)
                .x(x)
                .y(y)
                .build();
    }

    @Builder
    private Address(
            String addressName,
            String region1depthName,
            String region2depthName,
            String region3depthName,
            String roadName,
            BigDecimal x,
            BigDecimal y
    ) {
        this.addressName = addressName;
        this.region1depthName = region1depthName;
        this.region2depthName = region2depthName;
        this.region3depthName = region3depthName;
        this.roadName = roadName;
        this.coordinate = new Coordinate(x, y);
    }
}
