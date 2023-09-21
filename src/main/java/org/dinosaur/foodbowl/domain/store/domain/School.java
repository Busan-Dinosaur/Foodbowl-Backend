package org.dinosaur.foodbowl.domain.store.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.store.domain.vo.Coordinate;
import org.dinosaur.foodbowl.domain.store.domain.vo.SchoolName;
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

    @Valid
    @Embedded
    private SchoolName name;

    @NotNull
    @Column(name = "address_name", length = 512)
    private String addressName;

    @Valid
    @Embedded
    private Coordinate coordinate;

    @Builder
    private School(String name, String addressName, BigDecimal x, BigDecimal y) {
        this.name = new SchoolName(name);
        this.addressName = addressName;
        this.coordinate = new Coordinate(x, y);
    }

    public String getName() {
        return this.name.getValue();
    }

    public BigDecimal getX() {
        return this.coordinate.getX();
    }

    public BigDecimal getY() {
        return this.coordinate.getY();
    }
}
