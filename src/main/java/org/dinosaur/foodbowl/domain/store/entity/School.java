package org.dinosaur.foodbowl.domain.store.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.common.AuditingEntity;

@Getter
@Entity
@Table(name = "school")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class School extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", unique = true)
    private String name;

    @NotNull
    @DecimalMin(value = "-180", message = "경도의 최소값은 {value}입니다.")
    @DecimalMax(value = "180", message = "경도의 최대값은 {value}입니다.")
    @Column(name = "x", updatable = false)
    private BigDecimal x;

    @NotNull
    @DecimalMin(value = "-90", message = "위도의 최소값은 {value}입니다.")
    @DecimalMax(value = "90", message = "위도의 최대값은 {value}입니다.")
    @Column(name = "y", updatable = false)
    private BigDecimal y;

    @Builder
    private School(String name, BigDecimal x, BigDecimal y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }
}
