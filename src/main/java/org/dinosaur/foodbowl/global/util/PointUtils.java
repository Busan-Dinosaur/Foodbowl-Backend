package org.dinosaur.foodbowl.global.util;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointUtils {

    private static final int SR_ID = 4326;
    private static final GeometryFactory geometryFactory = new GeometryFactory();

    public static Point generate(BigDecimal x, BigDecimal y) {
        VerifiedCoordinate verifiedCoordinate = new VerifiedCoordinate(x, y);
        Coordinate coordinate = new Coordinate(verifiedCoordinate.getX(), verifiedCoordinate.getY());
        Point point = geometryFactory.createPoint(coordinate);
        point.setSRID(SR_ID);
        return point;
    }

    public static int getSrId() {
        return SR_ID;
    }
}
