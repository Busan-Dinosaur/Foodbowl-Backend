package org.dinosaur.foodbowl.test.persister;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.persistence.SchoolRepository;

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
        private BigDecimal x;
        private BigDecimal y;

        public SchoolBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SchoolBuilder addressName(String addressName) {
            this.addressName = addressName;
            return this;
        }

        public SchoolBuilder x(BigDecimal x) {
            this.x = x;
            return this;
        }

        public SchoolBuilder y(BigDecimal y) {
            this.y = y;
            return this;
        }

        public School save() {
            School school = School.builder()
                    .name(name == null ? RandomStringUtils.random(2, true, false) + "대학교" : name)
                    .addressName(addressName == null ? "서울시 영등포구 여의도동 451" : addressName)
                    .x(x == null ? BigDecimal.valueOf(123.1245) : x)
                    .y(y == null ? BigDecimal.valueOf(37.445) : y)
                    .build();
            return schoolRepository.save(school);
        }
    }
}
