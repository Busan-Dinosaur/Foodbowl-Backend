package org.dinosaur.foodbowl.domain.auth.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplePlatformUserResponse {

    private String socialId;
    private String email;
}
