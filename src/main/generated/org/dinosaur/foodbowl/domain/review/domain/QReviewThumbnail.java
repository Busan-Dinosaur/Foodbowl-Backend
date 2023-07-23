package org.dinosaur.foodbowl.domain.review.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviewThumbnail is a Querydsl query type for ReviewThumbnail
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewThumbnail extends EntityPathBase<ReviewThumbnail> {

    private static final long serialVersionUID = -730145168L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewThumbnail reviewThumbnail = new QReviewThumbnail("reviewThumbnail");

    public final org.dinosaur.foodbowl.global.persistence.QAuditingEntity _super = new org.dinosaur.foodbowl.global.persistence.QAuditingEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QReview review;

    public final org.dinosaur.foodbowl.domain.photo.domain.QThumbnail thumbnail;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QReviewThumbnail(String variable) {
        this(ReviewThumbnail.class, forVariable(variable), INITS);
    }

    public QReviewThumbnail(Path<? extends ReviewThumbnail> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewThumbnail(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewThumbnail(PathMetadata metadata, PathInits inits) {
        this(ReviewThumbnail.class, metadata, inits);
    }

    public QReviewThumbnail(Class<? extends ReviewThumbnail> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.review = inits.isInitialized("review") ? new QReview(forProperty("review"), inits.get("review")) : null;
        this.thumbnail = inits.isInitialized("thumbnail") ? new org.dinosaur.foodbowl.domain.photo.domain.QThumbnail(forProperty("thumbnail")) : null;
    }

}

