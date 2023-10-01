package org.dinosaur.foodbowl.domain.review.dto.request;

import java.math.BigDecimal;

public record MapCoordinateRequest(BigDecimal x, BigDecimal y, BigDecimal deltaX, BigDecimal deltaY) {
}
