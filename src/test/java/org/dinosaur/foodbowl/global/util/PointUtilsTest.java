package org.dinosaur.foodbowl.global.util;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PointUtilsTest {

    @Test
    void 좌표를_생성한다() {
        BigDecimal x = new BigDecimal(123.3636);
        BigDecimal y = new BigDecimal(32.3838);

        Point coordinate = PointUtils.generate(x, y);

        assertSoftly(softly -> {
            softly.assertThat(coordinate.getX()).isEqualTo(x.doubleValue());
            softly.assertThat(coordinate.getY()).isEqualTo(y.doubleValue());
            softly.assertThat(coordinate.getSRID()).isEqualTo(4326);
        });
    }
}
