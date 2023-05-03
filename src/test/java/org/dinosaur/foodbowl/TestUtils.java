package org.dinosaur.foodbowl;

import com.jayway.jsonpath.JsonPath;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {

    public static ResultMatcher jsonPathLocalDateTimeEquals(String expression, LocalDateTime expectedLocalDateTime) {
        return result -> {
            String content = result.getResponse().getContentAsString();
            String localDateTimeString = JsonPath.read(content, expression);
            LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            assertEquals(localDateTime, expectedLocalDateTime);
        };
    }
}
