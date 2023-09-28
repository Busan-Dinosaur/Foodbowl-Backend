package org.dinosaur.foodbowl.domain.member.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dinosaur.foodbowl.domain.member.domain.QMemberThumbnail.memberThumbnail;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.MemberThumbnail;
import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class MemberThumbnailRepositoryTest extends PersistenceTest {

    @Autowired
    private MemberThumbnailRepository memberThumbnailRepository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Nested
    class 멤버로_멤버_썸네일_조회_시 {

        @Test
        void 썸네일이_존재한다면_멤버_썸네일을_반환한다() {
            Member member = memberTestPersister.builder().save();
            MemberThumbnail memberThumbnail = memberThumbnailTestPersister.builder().member(member).save();

            Optional<MemberThumbnail> result = memberThumbnailRepository.findByMember(member);

            assertThat(result).containsSame(memberThumbnail);
        }

        @Test
        void 썸네일이_존재하지_않으면_빈_값을_반환한다() {
            Member member = memberTestPersister.builder().save();

            Optional<MemberThumbnail> result = memberThumbnailRepository.findByMember(member);

            assertThat(result).isNotPresent();
        }
    }

    @Test
    void 멤버_썸네일을_저장한다() {
        Member member = memberTestPersister.builder().save();
        Thumbnail thumbnail = thumbnailTestPersister.builder().save();
        MemberThumbnail memberThumbnail = MemberThumbnail.builder()
                .member(member)
                .thumbnail(thumbnail)
                .build();

        MemberThumbnail saveMemberThumbnail = memberThumbnailRepository.save(memberThumbnail);

        assertThat(saveMemberThumbnail.getId()).isNotNull();
    }

    @Test
    void 멤버_썸네일을_삭제한다() {
        MemberThumbnail saveMemberThumbnail = memberThumbnailTestPersister.builder().save();

        memberThumbnailRepository.delete(saveMemberThumbnail);

        List<MemberThumbnail> memberThumbnails = jpaQueryFactory.selectFrom(memberThumbnail)
                .where(memberThumbnail.id.eq(saveMemberThumbnail.getId()))
                .fetch();
        assertThat(memberThumbnails).isEmpty();
    }
}
