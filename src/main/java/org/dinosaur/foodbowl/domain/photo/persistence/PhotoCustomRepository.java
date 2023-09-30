package org.dinosaur.foodbowl.domain.photo.persistence;

import java.util.List;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;

public interface PhotoCustomRepository {

    long deleteByPhoto(Photo deletePhoto);

    long deleteAllByPhotos(List<Photo> photos);
}
