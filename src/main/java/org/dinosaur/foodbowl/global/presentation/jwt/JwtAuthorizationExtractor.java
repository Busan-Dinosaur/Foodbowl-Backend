package org.dinosaur.foodbowl.global.presentation.jwt;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthorizationExtractor {

    private static final String AUTHENTICATION_HEADER_KEY = "Authorization";
    private static final String AUTHENTICATION_TYPE = "Bearer";
    private static final String AUTHENTICATION_DELIMITER = " ";
    private static final int TOKEN_INDEX = 1;

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(AUTHENTICATION_HEADER_KEY);

        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (value.toLowerCase().startsWith(AUTHENTICATION_TYPE.toLowerCase())) {
                return Optional.ofNullable(value.split(AUTHENTICATION_DELIMITER)[TOKEN_INDEX]);
            }
        }
        return Optional.empty();
    }
}
