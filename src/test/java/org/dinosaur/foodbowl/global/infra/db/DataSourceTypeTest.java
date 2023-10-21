package org.dinosaur.foodbowl.global.infra.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.dinosaur.foodbowl.global.infra.db.DataSourceType.Key;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DataSourceTypeTest {

    @ParameterizedTest
    @EnumSource
    void 데이터_소스_타입_값을_가져온다(DataSourceType dataSourceType) {
        List<String> dataSourceTypes = List.of(Key.ROUTING_NAME, Key.REPLICA_NAME, Key.SOURCE_NAME);

        assertThat(dataSourceType.name()).isIn(dataSourceTypes);
    }
}
