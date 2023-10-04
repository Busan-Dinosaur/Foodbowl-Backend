package org.dinosaur.foodbowl.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.dinosaur.foodbowl.domain.follow.application.dto.MemberToFollowerCountDto;
import org.dinosaur.foodbowl.domain.follow.application.dto.MemberToFollowingsDto;
import org.dinosaur.foodbowl.domain.member.domain.Member;

@Schema(description = "회원 검색 응답 목록")
public record MemberSearchResponses(
        @Schema(description = "회원 검색 응답 결과")
        List<MemberSearchResponse> memberSearchResponses
) {

    public static MemberSearchResponses from(List<MemberSearchResponse> memberSearchResponses) {
        return new MemberSearchResponses(memberSearchResponses);
    }

    public static MemberSearchResponses of(
            List<Member> members,
            Member loginMember,
            MemberToFollowerCountDto followerCountByMembers,
            MemberToFollowingsDto memberToFollowings
    ) {
        List<MemberSearchResponse> memberSearchResponses = members.stream()
                .map(member -> MemberSearchResponse.of(
                        member,
                        loginMember,
                        followerCountByMembers.getFollowCount(member.getId()),
                        memberToFollowings.isFollowing(loginMember.getId(), member.getId())
                ))
                .toList();
        return new MemberSearchResponses(memberSearchResponses);
    }
}
