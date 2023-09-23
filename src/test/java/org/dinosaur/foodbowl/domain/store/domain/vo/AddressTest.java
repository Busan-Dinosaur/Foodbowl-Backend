package org.dinosaur.foodbowl.domain.store.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.dinosaur.foodbowl.global.util.PointUtils;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.locationtech.jts.geom.Point;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AddressTest {

    @Nested
    class 주소_생성_시 {

        @Test
        void 정상적인_요청이라면_주소를_생성한다() {
            String storeAddress = "서울시 강남구 테헤란로 17 강남빌딩 1201호";
            BigDecimal x = BigDecimal.valueOf(123.1234);
            BigDecimal y = BigDecimal.valueOf(36.12431);
            Point coordinate = PointUtils.generate(x, y);

            Address address = Address.of(storeAddress, coordinate);

            assertThat(address.getAddressName()).isEqualTo(storeAddress);
        }

        @ParameterizedTest
        @ValueSource(strings = {"부산시 동구", "서울시 강남구 삼성동", "", " "})
        void 정상적이지_않는_주소라면_예외를_던진다(String storeAddress) {
            BigDecimal x = BigDecimal.valueOf(123.1234);
            BigDecimal y = BigDecimal.valueOf(36.12431);
            Point coordinate = PointUtils.generate(x, y);

            assertThatThrownBy(() -> Address.of(storeAddress, coordinate))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("가게 주소 형식이 잘못되었습니다.");
        }

        @Test
        void 주소가_없으면_예외를_던진다() {
            BigDecimal x = BigDecimal.valueOf(123.1234);
            BigDecimal y = BigDecimal.valueOf(36.12431);
            Point coordinate = PointUtils.generate(x, y);

            assertThatThrownBy(() -> Address.of(null, coordinate))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("가게 주소가 존재하지 않습니다.");
        }
    }
}
