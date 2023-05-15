package org.dinosaur.foodbowl.global.config.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthorizationExtractor jwtAuthorizationExtractor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Optional<String> accessToken = jwtAuthorizationExtractor.extractAccessToken(request);

        if (accessToken.isPresent()) {
            Optional<Authentication> authentication = jwtTokenProvider.generateAuth(accessToken.get());
            authentication.ifPresent(auth -> SecurityContextHolder.getContext().setAuthentication(auth));
        }

        filterChain.doFilter(request, response);
    }
}
