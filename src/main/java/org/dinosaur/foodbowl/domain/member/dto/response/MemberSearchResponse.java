package org.dinosaur.foodbowl.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
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
        boolean isFollowing,

        @Schema(description = "검색한 사용자 여부", example = "false")
        boolean isMe
) {
    public static MemberSearchResponse of(
            Member member,
            Member loginMember,
            long followerCount,
            boolean isFollowing
    ) {
        return new MemberSearchResponse(
                member.getId(),
                member.getNickname(),
                member.getProfileImageUrl(),
                followerCount,
                isFollowing,
                Objects.equals(member, loginMember)
        );
    }
}
