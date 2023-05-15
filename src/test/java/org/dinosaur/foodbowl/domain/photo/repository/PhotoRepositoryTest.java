package org.dinosaur.foodbowl.domain.photo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PhotoRepositoryTest extends RepositoryTest {

    @Autowired
    private PhotoRepository photoRepository;

    @Test
    @DisplayName("게시글의 모든 사진을 삭제한다.")
    void deleteAllByPost() {
        Post post = postTestSupport.postBuilder().build();
        photoTestSupport.builder().post(post).build();

        photoRepository.deleteAllByPost(post);

        assertThat(photoRepository.findAll()).isEmpty();
    }
}
