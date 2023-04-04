package org.dinosaur.foodbowl.domain.health_check.dto;

import java.time.LocalDateTime;

public record HealthCheckDto(String message, LocalDateTime created) {

}
