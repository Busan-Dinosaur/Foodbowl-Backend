package org.dinosaur.foodbowl.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileUpdateRequest {

    @NotBlank(message = "닉네임은 존재해야 합니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣0-9]{1,16}$", message = "닉네임은 1자 이상 16자 이하 한글, 영문, 숫자만 가능합니다")
    private String nickname;

    @Size(max = 255, message = "소개는 최대 255자까지만 가능합니다.")
    private String introduction;
}
