package org.dinosaur.foodbowl.domain.auth.apple;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "apple-public-key-client", url = "https://appleid.apple.com")
public interface AppleClient {

    @GetMapping("/auth/keys")
    ApplePublicKeys getApplePublicKeys();
}
