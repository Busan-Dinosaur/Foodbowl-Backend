package org.dinosaur.foodbowl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class FoodbowlApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodbowlApplication.class, args);
    }
}
