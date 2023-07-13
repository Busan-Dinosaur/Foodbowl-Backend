package org.dinosaur.foodbowl.config.security.jwt;

public record JwtTokenValid(boolean isValid, String errorMessage, int code) {

}
