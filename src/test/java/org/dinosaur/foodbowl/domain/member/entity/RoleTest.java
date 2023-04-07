package org.dinosaur.foodbowl.domain.member.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoleTest {

    @DisplayName("역할 타입에 해당하는 역할을 생성한다.")
    @Test
    void createRoleByType() {
        Role role = Role.from(RoleType.ROLE_회원);

        assertAll(
                () -> assertThat(role.getId()).isEqualTo(1L),
                () -> assertThat(role.getRoleType()).isEqualTo(RoleType.ROLE_회원)
        );
    }
}
