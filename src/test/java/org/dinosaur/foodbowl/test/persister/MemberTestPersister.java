package org.dinosaur.foodbowl.test.persister;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;

@RequiredArgsConstructor
@Persister
public class MemberTestPersister {

    private final MemberRepository memberRepository;

    public MemberBuilder builder() {
        return new MemberBuilder();
    }

    public final class MemberBuilder {

        private SocialType socialType;
        private String socialId;
        private String email;
        private String nickname;
        private String introduction;

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

        public MemberBuilder introduction(String introduction) {
            this.introduction = introduction;
            return this;
        }

        public Member save() {
            Member member = Member.builder()
                    .socialType(socialType == null ? SocialType.APPLE : socialType)
                    .socialId(socialId == null ? RandomStringUtils.random(10, true, true) : socialId)
                    .email(email)
                    .nickname(nickname == null ? RandomStringUtils.random(10, true, false) : nickname)
                    .introduction(introduction)
                    .build();
            return memberRepository.save(member);
        }
    }
}
