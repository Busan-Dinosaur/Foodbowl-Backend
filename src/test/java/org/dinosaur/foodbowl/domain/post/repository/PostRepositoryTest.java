package org.dinosaur.foodbowl.domain.post.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

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

    @Nested
    @DisplayName("가게 정보를 포함하여 특정 멤버의 모든 게시글 조회 기능은 ")
    class FindPostsWithStoreOfMember {

        @Test
        @DisplayName("해당 멤버의 게시글만 조회한다.")
        void findPostsOfMember() {
            Member member = memberTestSupport.memberBuilder().build();
            postTestSupport.postBuilder().member(member).build();
            postTestSupport.postBuilder().build();

            List<Post> result = postRepository.findAllWithStoreByMember(member);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("게시글을 모두 조회한다.")
        void findAllPosts() {
            Member member = memberTestSupport.memberBuilder().build();
            postTestSupport.postBuilder().member(member).build();
            postTestSupport.postBuilder().member(member).build();

            List<Post> result = postRepository.findAllWithStoreByMember(member);

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("findAllByMember 메서드는 ")
    class FindAllByMember {

        @Test
        @DisplayName("해당 멤버의 게시글만 조회한다.")
        void findPostsByMember() {
            Member member = memberTestSupport.memberBuilder().build();
            Post post = postTestSupport.postBuilder().member(member).build();
            postTestSupport.postBuilder().build();

            Pageable pageable = PageRequest.of(0, 5, Sort.by(Direction.DESC, "createdAt"));
            Page<Post> result = postRepository.findAllByMember(member, pageable);

            List<Post> posts = result.getContent();
            assertAll(
                    () -> assertThat(posts).hasSize(1),
                    () -> assertThat(posts.get(0)).isEqualTo(post),
                    () -> assertThat(posts.get(0).getMember().getId()).isEqualTo(member.getId())
            );
        }

        @Test
        @DisplayName("페이징 설정만큼 게시글을 조회한다.")
        void findPostsByPaging() {
            Member member = memberTestSupport.memberBuilder().build();
            postTestSupport.postBuilder().member(member).build();
            postTestSupport.postBuilder().member(member).build();
            postTestSupport.postBuilder().member(member).build();

            Pageable pageable = PageRequest.of(0, 2, Sort.by(Direction.DESC, "createdAt"));
            Page<Post> result = postRepository.findAllByMember(member, pageable);

            List<Post> posts = result.getContent();
            assertAll(
                    () -> assertThat(posts).hasSize(2),
                    () -> assertThat(result.hasNext()).isTrue()
            );
        }
    }

    @Nested
    @DisplayName("findAll - pageable 메서드는 ")
    class FindAllPageable {

        @Test
        @DisplayName("전체 게시글을 조회한다.")
        void findAllPageable() {
            Post oldPost = postTestSupport.postBuilder().build();
            Post newPost = postTestSupport.postBuilder().build();

            Pageable pageable = PageRequest.of(0, 5, Sort.by(Direction.DESC, "id"));
            Page<Post> result = postRepository.findAll(pageable);

            List<Post> posts = result.getContent();
            assertAll(
                    () -> assertThat(posts).hasSize(2),
                    () -> assertThat(posts.get(0)).isEqualTo(newPost),
                    () -> assertThat(posts.get(1)).isEqualTo(oldPost)
            );
        }

        @Test
        @DisplayName("페이징 설정만큼 게시글을 조회한다.")
        void findAllPageableByPaging() {
            postTestSupport.postBuilder().build();
            Post newPost = postTestSupport.postBuilder().build();

            Pageable pageable = PageRequest.of(0, 1, Sort.by(Direction.DESC, "id"));
            Page<Post> result = postRepository.findAll(pageable);

            List<Post> posts = result.getContent();
            assertAll(
                    () -> assertThat(posts).hasSize(1),
                    () -> assertThat(posts.get(0)).isEqualTo(newPost)
            );
        }
    }
}
