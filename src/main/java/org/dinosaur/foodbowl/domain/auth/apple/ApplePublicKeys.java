package org.dinosaur.foodbowl.domain.auth.apple;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;

import java.util.List;

import static org.dinosaur.foodbowl.global.exception.ErrorStatus.APPLE_INVALID_HEADER;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplePublicKeys {

    private List<ApplePublicKey> keys;

    public ApplePublicKey getMatchingKey(String alg, String kid) {
        return keys.stream()
                .filter(key -> key.isSameAlg(alg) && key.isSameKid(kid))
                .findFirst()
                .orElseThrow(() -> new FoodbowlException(APPLE_INVALID_HEADER));
    }
}
