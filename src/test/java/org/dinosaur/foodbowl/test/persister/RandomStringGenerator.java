package org.dinosaur.foodbowl.test.persister;

import java.util.Random;
import java.util.stream.Collectors;

public class RandomStringGenerator {

    private static final Random RANDOM = new Random();

    public static String generateRandomString(int length) {
        char leftLimit = '0';
        char rightLimit = 'z';
        return RANDOM.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }
}
