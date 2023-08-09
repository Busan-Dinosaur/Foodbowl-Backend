package org.dinosaur.foodbowl.domain.photo.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.photo.persistence.PhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PhotoService {

    private final PhotoRepository photoRepository;

    @Transactional
    public void save(List<Photo> photos) {
        for (Photo photo : photos) {
            photoRepository.save(photo);
        }
    }
}
