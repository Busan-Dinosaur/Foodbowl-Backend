package org.dinosaur.foodbowl.testsupport;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.photo.entity.Thumbnail;
import org.dinosaur.foodbowl.domain.post.entity.Category;
import org.dinosaur.foodbowl.domain.post.entity.Category.CategoryType;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.dinosaur.foodbowl.domain.post.entity.PostCategory;
import org.dinosaur.foodbowl.domain.post.repository.PostCategoryRepository;
import org.dinosaur.foodbowl.domain.post.repository.PostRepository;
import org.dinosaur.foodbowl.domain.store.entity.Store;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostTestSupport {

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final MemberTestSupport memberTestSupport;
    private final ThumbnailTestSupport thumbnailTestSupport;
    private final StoreTestSupport storeTestSupport;

    public PostBuilder postBuilder() {
        return new PostBuilder();
    }

    public PostCategoryBuilder postCategoryBuilder() {
        return new PostCategoryBuilder();
    }

    public final class PostBuilder {

        private Member member;
        private Thumbnail thumbnail;
        private Store store;
        private String content;

        public PostBuilder member(Member member) {
            this.member = member;
            return this;
        }

        public PostBuilder thumbnail(Thumbnail thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public PostBuilder store(Store store) {
            this.store = store;
            return this;
        }

        public PostBuilder content(String content) {
            this.content = content;
            return this;
        }

        public Post build() {
            return postRepository.save(
                    Post.builder()
                            .member(member == null ? memberTestSupport.memberBuilder().build() : member)
                            .thumbnail(thumbnail == null ? thumbnailTestSupport.builder().build() : thumbnail)
                            .store(store == null ? storeTestSupport.builder().build() : store)
                            .content(content == null ? "content" + UUID.randomUUID() : content)
                            .build()
            );
        }
    }

    public final class PostCategoryBuilder {

        private Post post;
        private Category category;

        public PostCategoryBuilder post(Post post) {
            this.post = post;
            return this;
        }

        public PostCategoryBuilder category(Category category) {
            this.category = category;
            return this;
        }

        public PostCategory build() {
            return postCategoryRepository.save(
                    PostCategory.builder()
                            .post(post == null ? postBuilder().build() : post)
                            .category(category == null ? Category.from(CategoryType.전체) : category)
                            .build()
            );
        }
    }
}
