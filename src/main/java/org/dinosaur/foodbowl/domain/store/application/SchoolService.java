package org.dinosaur.foodbowl.domain.store.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.vo.SchoolName;
import org.dinosaur.foodbowl.domain.store.dto.response.SchoolsResponse;
import org.dinosaur.foodbowl.domain.store.exception.SchoolExceptionType;
import org.dinosaur.foodbowl.domain.store.persistence.SchoolRepository;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.util.PointUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SchoolService {

    private final SchoolRepository schoolRepository;

    @Transactional(readOnly = true)
    public Optional<School> findByName(String name) {
        return schoolRepository.findByName(new SchoolName(name));
    }

    @Transactional(readOnly = true)
    public SchoolsResponse getSchools() {
        List<School> schools = schoolRepository.findAllByOrderByName();
        return SchoolsResponse.from(schools);
    }

    @Transactional
    public School save(String name, String address, BigDecimal x, BigDecimal y) {
        schoolRepository.findByName(new SchoolName(name)).ifPresent(
                existingSchool -> {
                    throw new BadRequestException(SchoolExceptionType.DUPLICATE_SCHOOL);
                });

        School school = School.builder()
                .name(name)
                .addressName(address)
                .coordinate(PointUtils.generate(x, y))
                .build();
        return schoolRepository.save(school);
    }
}
