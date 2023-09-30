package org.dinosaur.foodbowl.domain.follow.application.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.MemberFollowerCountDto;

public record MemberToFollowerCountDto(
        Map<Long, Long> memberToFollowerCount
) {

    public static MemberToFollowerCountDto from(List<MemberFollowerCountDto> memberFollowerCounts) {
        Map<Long, Long> memberToFollowerCount = memberFollowerCounts.stream()
                .collect(
                        Collectors.toMap(
                                MemberFollowerCountDto::memberId,
                                MemberFollowerCountDto::followerCount,
                                (exist, replace) -> replace,
                                HashMap::new
                        )
                );
        return new MemberToFollowerCountDto(memberToFollowerCount);
    }

    public Long getFollowCount(Long memberId) {
        return memberToFollowerCount.getOrDefault(memberId, 0L);
    }
}
