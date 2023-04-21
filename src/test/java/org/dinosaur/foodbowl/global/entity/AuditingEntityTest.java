package org.dinosaur.foodbowl.global.entity;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.member.repository.MemberRepository;
import org.dinosaur.foodbowl.domain.photo.entity.Thumbnail;
import org.dinosaur.foodbowl.domain.photo.repository.ThumbnailRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class AuditingEntityTest extends RepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ThumbnailRepository thumbnailRepository;

    @Test
    @DisplayName("생성 시간만 존재하는 엔티티를 저장한다.")
    void saveEntityWithCreatedTime() {
        Thumbnail thumbnail = Thumbnail.builder()
                .path("static/foodbowl")
                .width(300)
                .height(300)
                .build();

        Thumbnail savedThumbnail = thumbnailRepository.save(thumbnail);

        assertThat(savedThumbnail.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("생성 시간과 수정 시간이 존재하는 엔티티를 저장한다.")
    void saveEntityWithCreatedTimeAndUpdatedTime() {
        Member member = Member.builder()
                .socialType(Member.SocialType.APPLE)
                .socialId("abc")
                .nickname("foodbowl")
                .build();

        Member savedMember = memberRepository.save(member);

        assertThat(savedMember.getCreatedAt()).isNotNull();
        assertThat(savedMember.getUpdatedAt()).isNotNull();
    }
}
