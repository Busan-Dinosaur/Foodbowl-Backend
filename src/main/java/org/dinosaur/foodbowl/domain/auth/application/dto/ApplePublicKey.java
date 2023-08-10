package org.dinosaur.foodbowl.domain.auth.application.dto;

public record ApplePublicKey(
        String kty,
        String kid,
        String use,
        String alg,
        String n,
        String e
) {

    public boolean isSameAlg(String alg) {
        return this.alg.equals(alg);
    }

    public boolean isSameKid(String kid) {
        return this.kid.equals(kid);
    }
}
