package org.dinosaur.foodbowl.test.persister;

import static org.dinosaur.foodbowl.test.persister.RandomStringGenerator.generateRandomString;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.springframework.stereotype.Component;

@Persister
@RequiredArgsConstructor
@Component
public class MemberTestPersister {

    private final MemberRepository memberRepository;

    public MemberBuilder memberBuilder() {
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
            return memberRepository.save(
                    Member.builder()
                            .socialType(socialType != null ? socialType : SocialType.APPLE)
                            .socialId(socialId != null ? socialId : generateRandomString(10))
                            .email(email)
                            .nickname(nickname != null ? nickname : generateRandomString(10))
                            .introduction(introduction)
                            .build()
            );
        }
    }
}
