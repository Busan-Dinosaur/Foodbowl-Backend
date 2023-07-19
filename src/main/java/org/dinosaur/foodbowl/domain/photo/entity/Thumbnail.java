package org.dinosaur.foodbowl.domain.photo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import org.dinosaur.foodbowl.domain.common.AuditingEntity;

@Getter
@Entity
@Table(name = "thumbnail")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Thumbnail extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @Column(name = "path", length = 512)
    private String path;

    @NotNull
    @Column(name = "width")
    private Integer width;

    @NotNull
    @Column(name = "height")
    private Integer height;

    @Builder
    private Thumbnail(String path, Integer width, Integer height) {
        this.path = path;
        this.width = width;
        this.height = height;
    }
}
