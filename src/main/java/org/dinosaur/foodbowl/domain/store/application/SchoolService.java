package org.dinosaur.foodbowl.domain.store.application;

import static org.dinosaur.foodbowl.domain.store.exception.SchoolExceptionType.*;

import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.persistence.SchoolRepository;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SchoolService {

    private final SchoolRepository schoolRepository;

    @Transactional
    public School save(String name, BigDecimal x, BigDecimal y) {
        schoolRepository.findByName(name).ifPresent(
                existingSchool -> {
                    throw new BadRequestException(DUPLICATE_ERROR);
                });

        School school = School.builder()
                .name(name)
                .x(x)
                .y(y)
                .build();
        return schoolRepository.save(school);
    }

    public Optional<School> findByName(String name) {
        return schoolRepository.findByName(name);
    }
}
