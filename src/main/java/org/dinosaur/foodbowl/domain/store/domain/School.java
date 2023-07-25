package org.dinosaur.foodbowl.domain.store.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.global.persistence.AuditingEntity;

@Getter
@Entity
@Table(name = "school")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class School extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @Column(name = "name", unique = true, length = 45)
    private String name;

    @NotNull
    @Column(name = "x", updatable = false)
    private BigDecimal x;

    @NotNull
    @Column(name = "y", updatable = false)
    private BigDecimal y;

    @Builder
    private School(String name, BigDecimal x, BigDecimal y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }
}
