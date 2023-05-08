package org.dinosaur.foodbowl.global.resolver;

import static org.dinosaur.foodbowl.global.exception.ErrorStatus.JWT_MALFORMED;
import static org.dinosaur.foodbowl.global.exception.ErrorStatus.JWT_NOT_FOUND;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtAuthorizationExtractor;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class MemberIdArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtAuthorizationExtractor jwtAuthorizationExtractor;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Long.class)
                && parameter.hasParameterAnnotation(MemberId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String accessToken =
                jwtAuthorizationExtractor.extractAccessToken(webRequest.getNativeRequest(HttpServletRequest.class))
                        .orElseThrow(() -> new FoodbowlException(JWT_NOT_FOUND));
        String memberId = jwtTokenProvider.extractSubject(accessToken)
                .orElseThrow(() -> new FoodbowlException(JWT_MALFORMED));
        return parseToLong(memberId);
    }
    
    private Long parseToLong(String memberId) {
        try {
            return Long.parseLong(memberId);
        } catch (NumberFormatException e) {
            throw new FoodbowlException(JWT_MALFORMED);
        }
    }
}
