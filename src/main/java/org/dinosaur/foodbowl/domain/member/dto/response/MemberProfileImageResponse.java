package org.dinosaur.foodbowl.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 프로필 이미지 응답")
public record MemberProfileImageResponse(
        @Schema(description = "프로필 이미지 URL", example = "https://justdoeat.shop/static/images/thumbnail/profile/image.png")
        String profileImageUrl
) {
}
