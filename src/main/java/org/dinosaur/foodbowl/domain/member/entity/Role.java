package org.dinosaur.foodbowl.domain.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "role")
@EqualsAndHashCode(of = {"id", "roleType"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    @Column(name = "name", updatable = false, unique = true, length = 45)
    private RoleType roleType;

    private Role(Long id, RoleType roleType) {
        this.id = id;
        this.roleType = roleType;
    }

    public static Role from(RoleType roleType) {
        return new Role(roleType.id, roleType);
    }

    public enum RoleType {

        ROLE_회원(1L),
        ROLE_관리자(2L),
        ;

        private final Long id;

        RoleType(final Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }
}
