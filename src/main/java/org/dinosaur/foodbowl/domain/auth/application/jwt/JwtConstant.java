package org.dinosaur.foodbowl.domain.auth.application.jwt;

import lombok.Getter;

@Getter
public enum JwtConstant {

    DELIMITER(","),
    CLAMS_ROLES("roles"),
    ACCESS_TOKEN("accessToken"),
    REFRESH_TOKEN("refreshToken");

    private final String name;

    JwtConstant(String name) {
        this.name = name;
    }
}
