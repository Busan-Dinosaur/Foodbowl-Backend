package org.dinosaur.foodbowl.domain.member.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.List;
import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.member.entity.Role;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RoleRepositoryTest extends RepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @DisplayName("DB에 존재하는 역할과 일치하는지 확인한다.")
    @Test
    void hasSameRoleWithDB() {
        List<Role> roles = roleRepository.findAll()
                .stream()
                .sorted(Comparator.comparingLong(Role::getId))
                .toList();
        RoleType[] roleTypes = RoleType.values();

        assertThat(roles.size()).isEqualTo(roleTypes.length);

        for (int i = 0; i < roles.size(); i++) {
            assertThat(roles.get(i).getId()).isEqualTo(roleTypes[i].getId());
            assertThat(roles.get(i).getRoleType()).isEqualTo(roleTypes[i]);
        }
    }
}
