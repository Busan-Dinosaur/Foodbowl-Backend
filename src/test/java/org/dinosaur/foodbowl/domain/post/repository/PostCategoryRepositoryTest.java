package org.dinosaur.foodbowl.domain.post.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostCategoryRepositoryTest extends RepositoryTest {

    @Autowired
    private PostCategoryRepository postCategoryRepository;

    @Test
    @DisplayName("게시글의 카테고리 목록을 삭제한다.")
    void deleteAllByPost() {
        Post post = postTestSupport.postBuilder().build();
        postTestSupport.postCategoryBuilder().post(post).build();

        postCategoryRepository.deleteAllByPost(post);

        assertThat(postCategoryRepository.findAll()).isEmpty();
    }
}
