package org.dinosaur.foodbowl.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.dinosaur.foodbowl.domain.member.domain.Member;

@Schema(description = "리뷰 작성자 응답")
public record ReviewWriterResponse(
        @Schema(description = "작성자 ID", example = "1")
        Long id,

        @Schema(description = "작성자 닉네임", example = "홍길동")
        String nickname,

        @Schema(description = "작성자 프로필 이미지 경로", example = "http://justdoeat.shop/image.png")
        String profileImageUrl,

        @Schema(description = "작성자 팔로우 수")
        long followerCount
) {

    public static ReviewWriterResponse of(Member member, long followerCount) {
        return new ReviewWriterResponse(
                member.getId(),
                member.getNickname(),
                member.getProfileImageUrl(),
                followerCount
        );
    }
}
