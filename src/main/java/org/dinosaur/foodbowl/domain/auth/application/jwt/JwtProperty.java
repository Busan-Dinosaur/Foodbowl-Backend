package org.dinosaur.foodbowl.domain.auth.application.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperty(String secretKey, long accessExpireTime, long refreshExpireTime) {
}
