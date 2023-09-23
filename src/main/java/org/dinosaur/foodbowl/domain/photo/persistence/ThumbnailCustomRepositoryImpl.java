package org.dinosaur.foodbowl.domain.photo.persistence;

import static org.dinosaur.foodbowl.domain.photo.domain.QThumbnail.thumbnail;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ThumbnailCustomRepositoryImpl implements ThumbnailCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public long deleteByThumbnail(Thumbnail deleteThumbnail) {
        return jpaQueryFactory.delete(thumbnail)
                .where(thumbnail.id.eq(deleteThumbnail.getId()))
                .execute();
    }
}
