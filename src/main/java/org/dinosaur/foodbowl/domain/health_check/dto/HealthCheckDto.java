package org.dinosaur.foodbowl.domain.health_check.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class HealthCheckDto {

    private String message;
    private LocalDateTime created;

    public HealthCheckDto(String message, LocalDateTime created) {
        this.message = message;
        this.created = created;
    }
}

