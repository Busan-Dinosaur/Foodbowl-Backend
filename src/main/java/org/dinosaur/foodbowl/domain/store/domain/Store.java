package org.dinosaur.foodbowl.domain.store.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.store.domain.vo.Address;
import org.dinosaur.foodbowl.global.persistence.AuditingEntity;

@Getter
@Entity
@Table(
        name = "store",
        indexes = {@Index(name = "IDX_STORE", columnList = "x, y")}
)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", updatable = false)
    private Category category;

    @NotNull
    @Column(name = "location_id", unique = true, length = 20)
    private String locationId;

    @NotNull
    @Column(name = "store_name", length = 100)
    private String storeName;

    @Valid
    @Embedded
    private Address address;

    @NotNull
    @Column(name = "store_url", length = 100)
    private String storeUrl;

    @Column(name = "phone", length = 45)
    private String phone;

    @Builder
    private Store(Category category, String storeName, Address address, String storeUrl, String phone) {
        this.category = category;
        this.storeName = storeName;
        this.address = address;
        this.storeUrl = storeUrl;
        this.phone = phone;
    }
}
