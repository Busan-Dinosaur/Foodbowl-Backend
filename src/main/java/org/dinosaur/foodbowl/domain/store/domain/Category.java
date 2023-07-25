package org.dinosaur.foodbowl.domain.store.domain;

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
import org.dinosaur.foodbowl.domain.store.domain.vo.CategoryType;

@Getter
@Entity
@Table(name = "category")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "name", updatable = false, unique = true, length = 45)
    private CategoryType categoryType;

    @Builder
    private Category(Long id, CategoryType categoryType) {
        this.id = id;
        this.categoryType = categoryType;
    }
}
