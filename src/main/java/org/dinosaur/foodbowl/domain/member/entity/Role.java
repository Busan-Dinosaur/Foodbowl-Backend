package org.dinosaur.foodbowl.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "role")
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "name", nullable = false, updatable = false, unique = true, length = 45)
    private RoleType roleType;

    private Role(Long id, RoleType roleType) {
        this.id = id;
        this.roleType = roleType;
    }

    public static Role from(RoleType roleType) {
        return new Role(roleType.id, roleType);
    }

    private enum RoleType {

        ROLE_회원(1L),
        ROLE_관리자(2L),
        ;

        private final Long id;

        RoleType(final Long id) {
            this.id = id;
        }
    }
}
