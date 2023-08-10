package org.dinosaur.foodbowl.domain.auth.application.dto;

import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;

public record AppleUser(SocialType socialType, String socialId, String email) {
}
