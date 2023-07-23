package org.dinosaur.foodbowl.testsupport;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.member.entity.Member.SocialType;
import org.dinosaur.foodbowl.domain.member.entity.MemberRole;
import org.dinosaur.foodbowl.domain.member.entity.Role;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.domain.member.repository.MemberRepository;
import org.dinosaur.foodbowl.domain.member.repository.MemberRoleRepository;
import org.dinosaur.foodbowl.domain.photo.entity.Thumbnail;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberTestSupport {

    private final MemberRepository memberRepository;
    private final MemberRoleRepository memberRoleRepository;

    public MemberBuilder memberBuilder() {
        return new MemberBuilder();
    }

    public MemberRoleBuilder memberRoleBuilder() {
        return new MemberRoleBuilder();
    }

    public final class MemberBuilder {

        private Thumbnail thumbnail;
        private SocialType socialType;
        private String socialId;
        private String email;
        private String nickname;
        private String introduce;
        private String region1depthName;
        private String region2depthName;

        public MemberBuilder thumbnail(Thumbnail thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public MemberBuilder socialType(SocialType socialType) {
            this.socialType = socialType;
            return this;
        }

        public MemberBuilder socialId(String socialId) {
            this.socialId = socialId;
            return this;
        }

        public MemberBuilder email(String email) {
            this.email = email;
            return this;
        }

        public MemberBuilder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public MemberBuilder introduce(String introduce) {
            this.introduce = introduce;
            return this;
        }

        public MemberBuilder region1depthName(String region1depthName) {
            this.region1depthName = region1depthName;
            return this;
        }

        public MemberBuilder region2depthName(String region2depthName) {
            this.region2depthName = region2depthName;
            return this;
        }

        public Member build() {
            return memberRepository.save(
                    Member.builder()
                            .thumbnail(thumbnail)
                            .socialType(socialType == null ? SocialType.APPLE : socialType)
                            .socialId(socialId == null ? "a1b2c3d4" : socialId)
                            .email(email)
                            .nickname(nickname == null ? "user" + UUID.randomUUID() : nickname)
                            .introduction(introduce)
                            .build()
            );
        }
    }

    public final class MemberRoleBuilder {

        private Member member;
        private Role role;

        public MemberRoleBuilder member(Member member) {
            this.member = member;
            return this;
        }

        public MemberRoleBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public MemberRole build() {
            return memberRoleRepository.save(
                    MemberRole.builder()
                            .member(member == null ? memberBuilder().build() : member)
                            .role(role == null ? Role.from(RoleType.ROLE_회원) : role)
                            .build()
            );
        }
    }
}
