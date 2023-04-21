package org.dinosaur.foodbowl.global.config.security.jwt;

public enum JwtConstant {

    DELIMITER(","),
    CLAMS_ROLES("roles"),
    ACCESS_TOKEN("accessToken"),
    REFRESH_TOKEN("refreshToken");

    private final String name;

    JwtConstant(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
