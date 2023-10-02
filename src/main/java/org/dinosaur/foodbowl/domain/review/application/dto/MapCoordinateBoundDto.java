package org.dinosaur.foodbowl.domain.review.application.dto;

import java.math.BigDecimal;
import org.dinosaur.foodbowl.global.util.PointUtils;
import org.locationtech.jts.geom.Point;

public record MapCoordinateBoundDto(
        Point topLeftPoint,
        Point topRightPoint,
        Point downLeftPoint,
        Point downRightPoint
) {

    public static MapCoordinateBoundDto of(BigDecimal x, BigDecimal y, BigDecimal deltaX, BigDecimal deltaY) {
        return new MapCoordinateBoundDto(
                PointUtils.generate(x.subtract(deltaX), y.add(deltaY)),
                PointUtils.generate(x.add(deltaX), y.add(deltaY)),
                PointUtils.generate(x.subtract(deltaX), y.subtract(deltaY)),
                PointUtils.generate(x.add(deltaX), y.subtract(deltaY))
        );
    }
}
