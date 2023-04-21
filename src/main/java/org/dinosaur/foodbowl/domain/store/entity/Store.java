package org.dinosaur.foodbowl.domain.store.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.dinosaur.foodbowl.global.entity.AuditingEntity;

@Getter
@Entity
@Table(name = "store")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Valid
    @Embedded
    private Address address;

    @NotNull
    @Column(name = "store_name", length = 100)
    private String storeName;

    @Builder
    private Store(Address address, String storeName) {
        this.address = address;
        this.storeName = storeName;
    }
}
