package org.dinosaur.foodbowl.domain.member.domain.vo;

import lombok.Getter;

@Getter
public enum RoleType {

    ROLE_회원(1L),
    ROLE_관리자(2L);

    private final Long id;

    RoleType(Long id) {
        this.id = id;
    }
}
