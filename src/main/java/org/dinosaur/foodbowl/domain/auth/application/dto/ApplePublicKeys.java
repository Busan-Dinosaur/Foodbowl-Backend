package org.dinosaur.foodbowl.domain.auth.application.dto;

import java.util.List;
import org.dinosaur.foodbowl.domain.auth.exception.AuthExceptionType;
import org.dinosaur.foodbowl.global.exception.BadRequestException;

public record ApplePublicKeys(List<ApplePublicKey> keys) {

    public ApplePublicKey getMatchingKey(String alg, String kid) {
        return keys.stream()
                .filter(key -> key.isSameAlg(alg) && key.isSameKid(kid))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(AuthExceptionType.UNSUPPORTED_JWT));
    }
}
