package org.dinosaur.foodbowl.domain.bookmark.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.global.persistence.AuditingEntity;

@Getter
@Entity
@Table(
        name = "bookmark",
        uniqueConstraints = {@UniqueConstraint(name = "uniq_bookmark", columnNames = {"member_id", "store_id"})}
)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", updatable = false)
    private Member member;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", updatable = false)
    private Store store;

    @Builder
    private Bookmark(Member member, Store store) {
        this.member = member;
        this.store = store;
    }
}
