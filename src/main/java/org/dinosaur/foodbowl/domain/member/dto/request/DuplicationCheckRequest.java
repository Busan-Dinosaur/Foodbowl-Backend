package org.dinosaur.foodbowl.domain.member.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DuplicationCheckRequest {

    @Pattern(regexp = "^[a-zA-Z가-힣]{1,10}$", message = "닉네임은 10자 이내 한글 또는 영문만 가능합니다.")
    private String nickname;
}
