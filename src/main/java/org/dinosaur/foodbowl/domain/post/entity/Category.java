package org.dinosaur.foodbowl.domain.post.entity;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "category")
@EqualsAndHashCode(of = {"id", "categoryType"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "name", length = 45)
    private CategoryType categoryType;

    private Category(Long id, CategoryType categoryType) {
        this.id = id;
        this.categoryType = categoryType;
    }

    public static Category from(CategoryType categoryType) {
        return new Category(categoryType.id, categoryType);
    }

    public enum CategoryType {

        전체(1L),
        카페(2L),
        한식(3L),
        양식(4L),
        일식(5L),
        중식(6L),
        치킨(7L),
        분식(8L),
        해산물(9L),
        샐러드(10L);

        private final Long id;

        CategoryType(final Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }
}
