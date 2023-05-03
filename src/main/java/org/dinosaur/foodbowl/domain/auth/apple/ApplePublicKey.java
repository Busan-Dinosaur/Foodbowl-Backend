package org.dinosaur.foodbowl.domain.auth.apple;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplePublicKey {

    private String kty;
    private String kid;
    private String use;
    private String alg;
    private String n;
    private String e;

    public boolean isSameAlg(String alg) {
        return this.alg.equals(alg);
    }

    public boolean isSameKid(String kid) {
        return this.kid.equals(kid);
    }
}
