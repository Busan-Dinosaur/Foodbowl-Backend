package org.dinosaur.foodbowl.test.persister;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.persistence.SchoolRepository;
import org.dinosaur.foodbowl.global.util.PointUtils;
import org.locationtech.jts.geom.Point;

@RequiredArgsConstructor
@Persister
public class SchoolTestPersister {

    private final SchoolRepository schoolRepository;

    public SchoolBuilder builder() {
        return new SchoolBuilder();
    }

    public class SchoolBuilder {

        private String name;
        private String addressName;
        private Point coordinate;

        public SchoolBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SchoolBuilder addressName(String addressName) {
            this.addressName = addressName;
            return this;
        }

        public SchoolBuilder coordinate(Point coordinate) {
            this.coordinate = coordinate;
            return this;
        }

        public School save() {
            School school = School.builder()
                    .name(name == null ? RandomStringUtils.random(2, true, false) + "대학교" : name)
                    .addressName(addressName == null ? "서울시 영등포구 여의도동 451" : addressName)
                    .coordinate(coordinate == null ?
                            PointUtils.generate(BigDecimal.valueOf(123.1245), BigDecimal.valueOf(37.445)) : coordinate
                    )
                    .build();
            return schoolRepository.save(school);
        }
    }
}
