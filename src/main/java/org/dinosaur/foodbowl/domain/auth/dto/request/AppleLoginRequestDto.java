package org.dinosaur.foodbowl.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppleLoginRequestDto {

    @NotBlank(message = "애플 토큰이 필요합니다.")
    private String appleToken;
}
