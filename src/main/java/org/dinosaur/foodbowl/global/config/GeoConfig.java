package org.dinosaur.foodbowl.global.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeoConfig {

    @PostConstruct
    public void setUp() {
        System.setProperty("org.geotools.referencing.forceXY", "true");
    }
}
