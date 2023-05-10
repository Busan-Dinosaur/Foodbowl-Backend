package org.dinosaur.foodbowl.testsupport;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.comment.entity.Comment;
import org.dinosaur.foodbowl.domain.comment.repository.CommentRepository;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentTestSupport {

    private final CommentRepository commentRepository;
    private final PostTestSupport postTestSupport;
    private final MemberTestSupport memberTestSupport;

    public CommentBuilder builder() {
        return new CommentBuilder();
    }

    public final class CommentBuilder {

        private Comment parent;
        private Post post;
        private Member member;
        private String message;

        public CommentBuilder parent(Comment parent) {
            this.parent = parent;
            return this;
        }

        public CommentBuilder post(Post post) {
            this.post = post;
            return this;
        }

        public CommentBuilder member(Member member) {
            this.member = member;
            return this;
        }

        public CommentBuilder message(String message) {
            this.message = message;
            return this;
        }

        public Comment build() {
            return commentRepository.save(
                    Comment.builder()
                            .parent(parent)
                            .post(post == null ? postTestSupport.postBuilder().build() : post)
                            .member(member == null ? memberTestSupport.memberBuilder().build() : member)
                            .message(message == null ? "message" + UUID.randomUUID() : message)
                            .build()
            );
        }
    }
}
