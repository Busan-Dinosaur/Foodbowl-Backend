package org.dinosaur.foodbowl.global.config.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.dinosaur.foodbowl.global.exception.ErrorStatus;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.stereotype.Component;

import java.util.Enumeration;

@Component
public class JwtAuthorizationExtractor {

    private static final String AUTHENTICATION_TYPE = "Bearer";
    private static final String AUTHENTICATION_HEADER_KEY = "Authorization";
    private static final int TOKEN_INDEX = 1;

    public String extractAccessToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(AUTHENTICATION_HEADER_KEY);

        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (value.toLowerCase().startsWith(AUTHENTICATION_TYPE.toLowerCase())) {
                return value.split(" ")[TOKEN_INDEX];
            }
        }
        throw new FoodbowlException(ErrorStatus.JWT_NOT_FOUND);
    }
}
