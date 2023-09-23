package org.dinosaur.foodbowl.domain.photo.persistence;

import org.dinosaur.foodbowl.domain.photo.domain.Photo;

public interface PhotoCustomRepository {

    long deleteByPhoto(Photo photo);
}
