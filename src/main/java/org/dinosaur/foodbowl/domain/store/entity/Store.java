package org.dinosaur.foodbowl.domain.store.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "store")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Embedded
    private Address address;

    @Column(name = "store_name", length = 100)
    private String name;

    @Builder
    private Store(Address address, String name) {
        this.address = address;
        this.name = name;
    }
}
