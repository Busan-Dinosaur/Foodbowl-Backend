package org.dinosaur.foodbowl.persister;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.persistence.SchoolRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SchoolTestPersister {

    private final SchoolRepository schoolRepository;

    public SchoolBuilder builder() {
        return new SchoolBuilder();
    }

    public class SchoolBuilder {

        public School build() {
            return schoolRepository.save(
                    School.builder()
                            .name("부산대학교")
                            .x(BigDecimal.valueOf(123.1245))
                            .y(BigDecimal.valueOf(37.445))
                            .build()
            );
        }
    }
}
