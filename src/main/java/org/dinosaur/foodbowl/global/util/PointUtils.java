package org.dinosaur.foodbowl.global.util;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.global.exception.ServerException;
import org.dinosaur.foodbowl.global.exception.type.ServerExceptionType;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointUtils {

    private static final int SR_ID = 4326;
    private static final CoordinateReferenceSystem crs;
    private static final GeometryFactory geometryFactory = new GeometryFactory();

    static {
        try {
            crs = CRS.decode("EPSG:4326");
        } catch (FactoryException e) {
            throw new ServerException(ServerExceptionType.INVALID_COORDINATE_CODE, e);
        }
    }

    public static Point generate(BigDecimal x, BigDecimal y) {
        VerifiedCoordinate verifiedCoordinate = new VerifiedCoordinate(x, y);
        Coordinate coordinate = new Coordinate(verifiedCoordinate.getX(), verifiedCoordinate.getY());
        Point point = geometryFactory.createPoint(coordinate);
        point.setSRID(SR_ID);
        return point;
    }

    public static double calculateDistance(Point src, Point dest) {
        try {
            return JTS.orthodromicDistance(src.getCoordinate(), dest.getCoordinate(), crs);
        } catch (Exception e) {
            throw new ServerException(ServerExceptionType.INVALID_COORDINATE_TRANSFORM, e);
        }
    }

    public static int getSrId() {
        return SR_ID;
    }
}
