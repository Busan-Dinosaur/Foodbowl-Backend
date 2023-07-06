package org.dinosaur.foodbowl.domain.post.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.bookmark.entity.Bookmark;
import org.dinosaur.foodbowl.domain.comment.entity.Comment;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.photo.entity.Thumbnail;
import org.dinosaur.foodbowl.domain.store.entity.Store;
import org.dinosaur.foodbowl.global.entity.AuditingEntity;

@Getter
@Entity
@Table(name = "post")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @NotNull
    @JoinColumn(name = "thumbnail_id")
    private Thumbnail thumbnail;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "store_id")
    private Store store;

    @NotNull
    @Column(name = "content", length = 2000)
    private String content;

    @OneToMany(mappedBy = "post")
    private final List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private final List<Bookmark> bookmarks = new ArrayList<>();

    @Builder
    private Post(Member member, Thumbnail thumbnail, Store store, String content) {
        this.member = member;
        this.thumbnail = thumbnail;
        this.store = store;
        this.content = content;
    }
}
