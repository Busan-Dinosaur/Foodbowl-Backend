package org.dinosaur.foodbowl.domain.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberThumbnailTest {

    @Test
    void 멤버_썸네일을_생성한다() {
        Member member = Member.builder()
                .socialType(SocialType.APPLE)
                .socialId("1")
                .email("email@email.com")
                .nickname("hello")
                .introduction("hello world")
                .build();
        Thumbnail thumbnail = Thumbnail.builder()
                .path("http://foodbowl.com/static/images/image.png")
                .build();
        MemberThumbnail memberThumbnail = MemberThumbnail.builder()
                .member(member)
                .thumbnail(thumbnail)
                .build();

        assertThat(memberThumbnail.getMember()).isEqualTo(member);
    }
}
