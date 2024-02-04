package org.dinosaur.foodbowl.domain.store.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.store.exception.StoreExceptionType;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.locationtech.jts.geom.Point;

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

    @NotNull
    @Column(name = "coordinate", columnDefinition = "geometry(Point, 4326)")
    private Point coordinate;

    @Builder
    private Address(
            String addressName,
            String region1depthName,
            String region2depthName,
            String region3depthName,
            String roadName,
            Point coordinate
    ) {
        this.addressName = addressName;
        this.region1depthName = region1depthName;
        this.region2depthName = region2depthName;
        this.region3depthName = region3depthName;
        this.roadName = roadName;
        this.coordinate = coordinate;
    }

    public static Address of(String storeAddress, Point coordinate) {
        if (storeAddress == null) {
            throw new InvalidArgumentException(StoreExceptionType.ADDRESS_NOT_FOUND);
        }

        List<String> addressElements = Arrays.stream(storeAddress.split(DELIMITER)).toList();
        if (addressElements.size() < MIN_SIZE) {
            throw new InvalidArgumentException(StoreExceptionType.INVALID_ADDRESS);
        }
        String roadName = String.join(DELIMITER, addressElements.subList(3, addressElements.size()));
        return Address.builder()
                .addressName(storeAddress)
                .region1depthName(addressElements.get(0))
                .region2depthName(addressElements.get(1))
                .region3depthName(addressElements.get(2))
                .roadName(roadName)
                .coordinate(coordinate)
                .build();
    }
}
