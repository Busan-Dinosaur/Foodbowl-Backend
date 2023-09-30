package org.dinosaur.foodbowl.global.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
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

    @Test
    void 좌표간_거리를_계산한다() {
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
}
