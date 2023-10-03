package org.dinosaur.foodbowl.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "회원 검색 응답 목록")
public record MemberSearchResponses(
        @Schema(description = "회원 검색 응답 결과")
        List<MemberSearchResponse> memberSearchResponses
) {

    public static MemberSearchResponses from(List<MemberSearchResponse> memberSearchResponses) {
        return new MemberSearchResponses(memberSearchResponses);
    }
}
