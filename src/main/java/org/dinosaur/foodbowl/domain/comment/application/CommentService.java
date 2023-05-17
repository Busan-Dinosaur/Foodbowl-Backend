package org.dinosaur.foodbowl.domain.comment.application;

import static org.dinosaur.foodbowl.global.exception.ErrorStatus.MEMBER_NOT_FOUND;
import static org.dinosaur.foodbowl.global.exception.ErrorStatus.POST_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.comment.dto.CommentRequest;
import org.dinosaur.foodbowl.domain.comment.dto.CommentResponse;
import org.dinosaur.foodbowl.domain.comment.entity.Comment;
import org.dinosaur.foodbowl.domain.comment.repository.CommentRepository;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.member.repository.MemberRepository;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.dinosaur.foodbowl.domain.post.repository.PostRepository;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponse save(Long memberId, CommentRequest commentRequest) {
        Long postId = commentRequest.getPostId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new FoodbowlException(POST_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new FoodbowlException(MEMBER_NOT_FOUND));

        return saveComment(post, member, commentRequest.getMessage());
    }

    private CommentResponse saveComment(Post post, Member member, String message) {
        Comment comment = Comment.builder()
                .parent(null)
                .post(post)
                .member(member)
                .message(message)
                .build();
        Comment savedComment = commentRepository.save(comment);
        return CommentResponse.from(savedComment);
    }
}
