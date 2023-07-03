package org.dinosaur.foodbowl.domain.member.application;

import static org.dinosaur.foodbowl.global.exception.ErrorStatus.MEMBER_NOT_FOUND;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.blame.entity.Blame.BlameTarget;
import org.dinosaur.foodbowl.domain.blame.repository.BlameRepository;
import org.dinosaur.foodbowl.domain.bookmark.repository.BookmarkRepository;
import org.dinosaur.foodbowl.domain.comment.repository.CommentRepository;
import org.dinosaur.foodbowl.domain.follow.repository.FollowRepository;
import org.dinosaur.foodbowl.domain.member.dto.request.ProfileUpdateRequest;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileResponse;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.member.repository.MemberRepository;
import org.dinosaur.foodbowl.domain.member.repository.MemberRoleRepository;
import org.dinosaur.foodbowl.domain.photo.repository.PhotoRepository;
import org.dinosaur.foodbowl.domain.photo.repository.ThumbnailRepository;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.dinosaur.foodbowl.domain.post.repository.PostCategoryRepository;
import org.dinosaur.foodbowl.domain.post.repository.PostRepository;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final FollowRepository followRepository;
    private final ThumbnailRepository thumbnailRepository;
    private final PhotoRepository photoRepository;
    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final CommentRepository commentRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BlameRepository blameRepository;

    @Transactional(readOnly = true)
    public MemberProfileResponse getMemberProfile(Long profileMemberId, Long memberId) {
        Member profileMember = memberRepository.findById(profileMemberId)
                .orElseThrow(() -> new FoodbowlException(MEMBER_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new FoodbowlException(MEMBER_NOT_FOUND));

        boolean isSelfProfile = Objects.equals(profileMember, member);
        boolean isFollowed = profileMember.getFollowers()
                .stream()
                .anyMatch(follow -> follow.isFollower(member));

        return new MemberProfileResponse(
                profileMember.getNickname(),
                profileMember.getThumbnailPath(),
                profileMember.getFollowers().size(),
                profileMember.getFollowings().size(),
                isSelfProfile,
                isFollowed
        );
    }

    @Transactional
    public void updateProfile(Long memberId, ProfileUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new FoodbowlException(MEMBER_NOT_FOUND));

        member.updateProfile(request.getNickname(), request.getIntroduction());
    }

    @Transactional
    public void withDraw(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new FoodbowlException(MEMBER_NOT_FOUND));
        memberRoleRepository.deleteAllByMember(member);
        followRepository.deleteAllByFollower(member);
        followRepository.deleteAllByFollowing(member);
        blameRepository.deleteAllByMember(member);
        blameRepository.deleteAllByTargetIdAndBlameTarget(member.getId(), BlameTarget.MEMBER);
        bookmarkRepository.deleteAllByMember(member);
        commentRepository.deleteAllByMember(member);
        for (Post post : member.getPosts()) {
            bookmarkRepository.deleteAllByPost(post);
            commentRepository.deleteAllByPost(post);
            photoRepository.deleteAllByPost(post);
            postCategoryRepository.deleteAllByPost(post);
            postRepository.delete(post);
            thumbnailRepository.delete(post.getThumbnail());
        }
        memberRepository.delete(member);
        thumbnailRepository.delete(member.getThumbnail());
    }
}
