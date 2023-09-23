package org.dinosaur.foodbowl.domain.member.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dinosaur.foodbowl.domain.member.domain.QMemberThumbnail.memberThumbnail;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.MemberThumbnail;
import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class MemberThumbnailRepositoryTest extends PersistenceTest {

    @Autowired
    private MemberThumbnailRepository memberThumbnailRepository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

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
