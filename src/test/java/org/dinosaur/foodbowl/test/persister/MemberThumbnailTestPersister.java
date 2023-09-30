package org.dinosaur.foodbowl.test.persister;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.MemberThumbnail;
import org.dinosaur.foodbowl.domain.member.persistence.MemberThumbnailRepository;
import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;

@RequiredArgsConstructor
@Persister
public class MemberThumbnailTestPersister {

    private final MemberThumbnailRepository memberThumbnailRepository;
    private final MemberTestPersister memberTestPersister;
    private final ThumbnailTestPersister thumbnailTestPersister;

    public MemberThumbnailBuilder builder() {
        return new MemberThumbnailBuilder();
    }

    public final class MemberThumbnailBuilder {

        private Member member;
        private Thumbnail thumbnail;

        public MemberThumbnailBuilder member(Member member) {
            this.member = member;
            return this;
        }

        public MemberThumbnailBuilder thumbnail(Thumbnail thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public MemberThumbnail save() {
            MemberThumbnail memberThumbnail = MemberThumbnail.builder()
                    .member(member == null ? memberTestPersister.builder().save() : member)
                    .thumbnail(thumbnail == null ? thumbnailTestPersister.builder().save() : thumbnail)
                    .build();
            return memberThumbnailRepository.save(memberThumbnail);
        }
    }
}
