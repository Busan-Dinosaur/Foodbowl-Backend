package org.dinosaur.foodbowl.domain.follow.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.dinosaur.foodbowl.domain.member.domain.Member;

@Schema(description = "다른 회원 팔로워 응답")
public record OtherUserFollowerResponse(
        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "이미지 URL", example = "http://justdoeat.shop/static/images/profile.png")
        String profileImageUrl,

        @Schema(description = "닉네임", example = "coby5502")
        String nickname,

        @Schema(description = "팔로워 수", example = "20")
        int followerCount,

        @Schema(description = "팔로잉 여부", example = "true")
        boolean isFollowing
) {

    public static OtherUserFollowerResponse of(Member follower, Member searchMember) {
        return new OtherUserFollowerResponse(
                follower.getId(),
                follower.getProfileImageUrl(),
                follower.getNickname(),
                follower.getFollowerCount(),
                searchMember.isFollowing(follower)
        );
    }
}
