package org.dinosaur.foodbowl.domain.member.entity;

import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RoleTest {

    @DisplayName("역할 타입에 해당하는 역할을 생성한다.")
    @EnumSource
    @ParameterizedTest
    void createRoleByType(RoleType roleType) {
        Role role = Role.from(roleType);

        assertAll(
                () -> assertThat(role.getId()).isEqualTo(roleType.getId()),
                () -> assertThat(role.getRoleType()).isEqualTo(roleType)
        );
    }
}
