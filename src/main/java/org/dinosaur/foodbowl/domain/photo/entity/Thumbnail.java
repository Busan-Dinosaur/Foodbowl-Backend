package org.dinosaur.foodbowl.domain.photo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.dinosaur.foodbowl.global.entity.AuditingEntity;

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
