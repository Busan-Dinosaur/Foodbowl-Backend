package org.dinosaur.foodbowl.global.config.security.jwt;

public record JwtTokenValid(boolean isValid, String errorMessage, int code) {

}
