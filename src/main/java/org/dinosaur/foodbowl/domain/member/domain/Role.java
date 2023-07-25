package org.dinosaur.foodbowl.domain.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.member.domain.vo.RoleType;

@Getter
@Entity
@Table(name = "role")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "name", updatable = false, unique = true, length = 45)
    private RoleType roleType;

    @Builder
    private Role(Long id, RoleType roleType) {
        this.id = id;
        this.roleType = roleType;
    }
}
