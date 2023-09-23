package org.dinosaur.foodbowl.domain.photo.persistence;

import static org.dinosaur.foodbowl.domain.photo.domain.QPhoto.photo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PhotoCustomRepositoryImpl implements PhotoCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public long deleteByPhoto(Photo deletePhoto) {
        return jpaQueryFactory.delete(photo)
                .where(photo.id.eq(deletePhoto.getId()))
                .execute();
    }
}
