package org.dinosaur.foodbowl.domain.auth.application.jwt;

public record JwtTokenValid(boolean isValid, String errorCode, String message) {
}
