package org.dinosaur.foodbowl.domain.auth.jwt;

public record JwtTokenValid(boolean isValid, String errorCode, String message) {
}
