package org.dinosaur.foodbowl.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.dinosaur.foodbowl.domain.member.domain.Member;

@Schema(description = "회원 프로필 응답")
public record MemberProfileResponse(
        @Schema(description = "회원 ID", example = "1")
        Long id,

        @Schema(description = "프로필 이미지 URL", example = "https://justdoeat.shop/static/images/thumbnail/profile/image.png")
        String profileImageUrl,

        @Schema(description = "닉네임", example = "coby5502")
        String nickname,

        @Schema(description = "한 줄 소개", example = "동네 맛집 탐험을 좋아하는 아저씨에요.")
        String introduction,

        @Schema(description = "팔로워 수", example = "100")
        int followerCount,

        @Schema(description = "팔로잉 수", example = "100")
        int followingCount,

        @Schema(description = "본인 프로필 여부", example = "false")
        boolean isMyProfile,

        @Schema(description = "팔로잉 여부", example = "true")
        boolean isFollowing
) {

    public static MemberProfileResponse of(
            Member member,
            int followingCount,
            boolean isMyProfile,
            boolean isFollowing
    ) {
        return new MemberProfileResponse(
                member.getId(),
                member.getProfileImageUrl(),
                member.getNickname(),
                member.getIntroduction(),
                member.getFollowerCount(),
                followingCount,
                isMyProfile,
                isMyProfile ? false : isFollowing
        );
    }
}
