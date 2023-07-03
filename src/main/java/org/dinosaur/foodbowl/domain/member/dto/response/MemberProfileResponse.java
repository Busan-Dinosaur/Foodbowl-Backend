package org.dinosaur.foodbowl.domain.member.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfileResponse {

    private String nickname;
    private String thumbnailPath;
    private int numberOfFollower;
    private int numberOfFollowing;
    private Boolean isSelfProfile;
    private Boolean isFollowed;
}
