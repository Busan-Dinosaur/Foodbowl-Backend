package org.dinosaur.foodbowl.domain.auth.application.jwt;

import org.dinosaur.foodbowl.global.exception.ExceptionType;

public record JwtTokenValid(boolean isValid, ExceptionType exceptionType) {
}
