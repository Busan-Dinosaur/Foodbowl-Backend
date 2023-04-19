package org.dinosaur.foodbowl.domain.member.repository;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.member.entity.Role;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoleRepositoryTest extends RepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @DisplayName("DB에 존재하는 역할과 일치하는지 확인한다.")
    @Test
    void hasSameRoleWithDB() {
        List<Role> enumRoles = Arrays.stream(RoleType.values())
                .map(Role::from)
                .toList();

        List<Role> dbRoles = roleRepository.findAll()
                .stream()
                .sorted(Comparator.comparingLong(Role::getId))
                .toList();

        assertThat(enumRoles).isEqualTo(dbRoles);
    }
}
