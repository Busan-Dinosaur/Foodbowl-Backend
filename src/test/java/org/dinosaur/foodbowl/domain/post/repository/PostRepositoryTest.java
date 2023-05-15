package org.dinosaur.foodbowl.domain.post.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostRepositoryTest extends RepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("게시글을 삭제한다.")
    void delete() {
        Post post = postTestSupport.postBuilder().build();

        postRepository.delete(post);

        assertThat(postRepository.findAll()).isEmpty();
    }
}
