package org.dinosaur.foodbowl.global.config.web;

import org.dinosaur.foodbowl.FoodbowlApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = FoodbowlApplication.class)
public class FeignClientConfig {
}