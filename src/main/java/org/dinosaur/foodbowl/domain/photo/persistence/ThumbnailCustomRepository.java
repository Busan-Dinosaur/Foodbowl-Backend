package org.dinosaur.foodbowl.domain.photo.persistence;

import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;

public interface ThumbnailCustomRepository {

    long deleteByThumbnail(Thumbnail thumbnail);
}
