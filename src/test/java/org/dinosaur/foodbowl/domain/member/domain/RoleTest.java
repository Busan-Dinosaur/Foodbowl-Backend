package org.dinosaur.foodbowl.domain.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.domain.member.domain.vo.RoleType;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RoleTest {

    @Test
    void 역할을_생성한다() {
        Role role = Role.builder()
                .id(1L)
                .roleType(RoleType.ROLE_회원)
                .build();

        assertThat(role.getRoleType()).isEqualTo(RoleType.ROLE_회원);
    }
}
