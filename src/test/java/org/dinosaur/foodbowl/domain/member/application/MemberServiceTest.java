package org.dinosaur.foodbowl.domain.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import jakarta.persistence.EntityManager;
import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.blame.repository.BlameRepository;
import org.dinosaur.foodbowl.domain.bookmark.repository.BookmarkRepository;
import org.dinosaur.foodbowl.domain.comment.repository.CommentRepository;
import org.dinosaur.foodbowl.domain.follow.repository.FollowRepository;
import org.dinosaur.foodbowl.domain.member.dto.response.NicknameDuplicateCheckResponse;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.member.repository.MemberRepository;
import org.dinosaur.foodbowl.domain.member.repository.MemberRoleRepository;
import org.dinosaur.foodbowl.domain.photo.entity.Thumbnail;
import org.dinosaur.foodbowl.domain.photo.repository.PhotoRepository;
import org.dinosaur.foodbowl.domain.photo.repository.ThumbnailRepository;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.dinosaur.foodbowl.domain.post.repository.PostCategoryRepository;
import org.dinosaur.foodbowl.domain.post.repository.PostRepository;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceTest extends IntegrationTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private EntityManager em;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberRoleRepository memberRoleRepository;
    @Autowired
    private ThumbnailRepository thumbnailRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private BlameRepository blameRepository;
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostCategoryRepository postCategoryRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PhotoRepository photoRepository;

    @Nested
    @DisplayName("회원 탈퇴 시 ")
    class WithDraw {

        @Test
        @DisplayName("멤버의 모든 정보를 삭제한다.")
        void withDraw() {
            Thumbnail memberThumbnail = thumbnailTestSupport.builder().build(); //썸네일1
            Thumbnail postThumbnail = thumbnailTestSupport.builder().build(); //썸네일1
            Member member = memberTestSupport.memberBuilder().thumbnail(memberThumbnail).build(); //멤버1
            Post post = postTestSupport.postBuilder().thumbnail(postThumbnail).member(member).build(); //가게1, 게시글1
            postTestSupport.postCategoryBuilder().post(post).build(); //게시글 카테고리1
            memberTestSupport.memberRoleBuilder().member(member).build(); //멤버 역할1
            followTestSupport.builder().following(member).build(); //멤버1, 팔로우1
            followTestSupport.builder().follower(member).build(); //멤버1, 팔로우1
            blameTestSupport.builder().member(member).build(); //신고1
            bookmarkTestSupport.builder().member(member).build(); //멤버1, 썸네일1, 가게1, 게시글1, 북마크1
            bookmarkTestSupport.builder().post(post).build(); //멤버1, 북마크1
            commentTestSupport.builder().member(member).build(); //멤버1, 썸네일1, 가게1, 게시글1, 댓글1
            commentTestSupport.builder().post(post).build(); //멤버1, 댓글1
            photoTestSupport.builder().post(post).build(); //사진1

            em.flush();
            em.clear();

            memberService.withDraw(member.getId());

            assertAll(
                    () -> assertThat(memberRepository.findAll()).hasSize(6),
                    () -> assertThat(postRepository.findAll()).hasSize(2),
                    () -> assertThat(thumbnailRepository.findAll()).hasSize(2),
                    () -> assertThat(memberRoleRepository.findAll()).isEmpty(),
                    () -> assertThat(followRepository.findAll()).isEmpty(),
                    () -> assertThat(blameRepository.findAll()).isEmpty(),
                    () -> assertThat(bookmarkRepository.findAll()).isEmpty(),
                    () -> assertThat(postCategoryRepository.findAll()).isEmpty(),
                    () -> assertThat(commentRepository.findAll()).isEmpty(),
                    () -> assertThat(photoRepository.findAll()).isEmpty()
            );
        }

        @Test
        @DisplayName("멤버가 존재하지 않으면 예외를 던진다.")
        void withDrawWithNotExistMember() {
            assertThatThrownBy(() -> memberService.withDraw(-1L))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }
    }
  
    @Nested
    @DisplayName("checkDuplicate 메서드는")
    class checkDuplicate {

        @Test
        @DisplayName("닉네임과 일치하는 회원이 존재하면 true를 반환한다.")
        void checkDuplicateMember() {
            String nickname = "gray";
            Member member = Member.builder()
                    .socialType(Member.SocialType.APPLE)
                    .socialId("1234")
                    .nickname(nickname)
                    .build();
            memberRepository.save(member);

            NicknameDuplicateCheckResponse nicknameDuplicateCheckResponse = memberService.checkDuplicate(nickname);

            assertThat(nicknameDuplicateCheckResponse.isHasDuplicate()).isTrue();
        }

        @Test
        @DisplayName("닉네임과 일치하는 회원이 존재하면 false를 반환한다.")
        void checkNoneDuplicateMember() {
            String nickname = "gray";
            Member member = Member.builder()
                    .socialType(Member.SocialType.APPLE)
                    .socialId("1234")
                    .nickname("dazzle")
                    .build();
            memberRepository.save(member);

            NicknameDuplicateCheckResponse nicknameDuplicateCheckResponse = memberService.checkDuplicate(nickname);

            assertThat(nicknameDuplicateCheckResponse.isHasDuplicate()).isFalse();
        }
    }
}
