package org.dinosaur.foodbowl.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.dinosaur.foodbowl.domain.follow.application.dto.MemberToFollowerCountDto;
import org.dinosaur.foodbowl.domain.member.domain.Member;

@Schema(description = "회원 검색 응답")
public record MemberSearchResponse(
        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "닉네임", example = "coby5502")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://justdoeat.shop/static/images/thumbnail/profile/image.png")
        String profileImageUrl,

        @Schema(description = "팔로워 수", example = "100")
        long followerCount,

        @Schema(description = "팔로잉 여부", example = "true")
        boolean isFollowing
) {
    public static MemberSearchResponse of(
            Member member,
            boolean isFollowing,
            MemberToFollowerCountDto memberToFollowerCountDto
    ) {
        return new MemberSearchResponse(
                member.getId(),
                member.getNickname(),
                member.getProfileImageUrl(),
                memberToFollowerCountDto.getFollowCount(member.getId()),
                isFollowing
        );
    }
}
