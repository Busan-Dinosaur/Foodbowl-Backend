package org.dinosaur.foodbowl.global.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import org.dinosaur.foodbowl.global.exception.ServerException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PointUtilsTest {

    @BeforeAll
    public static void init() {
        System.setProperty("org.geotools.referencing.forceXY", "true");
    }

    @Test
    void 좌표를_생성한다() {
        BigDecimal x = BigDecimal.valueOf(123.3636);
        BigDecimal y = BigDecimal.valueOf(32.3838);

        Point coordinate = PointUtils.generate(x, y);

        assertSoftly(softly -> {
            softly.assertThat(coordinate.getX()).isEqualTo(x.doubleValue());
            softly.assertThat(coordinate.getY()).isEqualTo(y.doubleValue());
            softly.assertThat(coordinate.getSRID()).isEqualTo(4326);
        });
    }

    @Nested
    class 좌표간_거리_계산_시 {

        @Test
        void 좌표_시스템이_동일하면_거리를_계산한다() {
            //신림역 7번 출구
            BigDecimal x1 = BigDecimal.valueOf(126.92982526236122);
            BigDecimal y1 = BigDecimal.valueOf(37.48468344846119);
            //잠실역 8번 출구
            BigDecimal x2 = BigDecimal.valueOf(127.10106420114704);
            BigDecimal y2 = BigDecimal.valueOf(37.513826623937156);
            Point pointA = PointUtils.generate(x1, y1);
            Point pointB = PointUtils.generate(x2, y2);

            double result = PointUtils.calculateDistance(pointA, pointB);

            //카카오 맵 기준 23.7km(일직선 상 거리가 아님)
            //일직선 상 거리 15.48km
            assertThat(Math.round(result / 10) * 10).isEqualTo(15480);
        }

        @Test
        void 좌표_시스템이_올바르지_않으면_예외를_던진다() {
            GeometryFactory geometryFactory = new GeometryFactory();
            Coordinate coordinateA = new Coordinate(126.92982526236122, 37.48468344846119);
            Coordinate coordinateB = new Coordinate(37.513826623937156, 127.10106420114704);
            Point pointA = geometryFactory.createPoint(coordinateA);
            Point pointB = geometryFactory.createPoint(coordinateB);
            pointA.setSRID(4326);
            pointB.setSRID(0);

            assertThatThrownBy(() -> PointUtils.calculateDistance(pointA, pointB))
                    .isInstanceOf(ServerException.class)
                    .hasMessage("좌표 거리 계산 중 에러가 발생하였습니다.");
        }
    }
}
