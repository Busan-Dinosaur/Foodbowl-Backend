package org.dinosaur.foodbowl.global.presentation.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("monitor")
public record ActuatorProperties(
        String pattern,
        String user,
        String password,
        String role
) {
}
