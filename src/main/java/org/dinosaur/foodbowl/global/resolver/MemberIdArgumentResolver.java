package org.dinosaur.foodbowl.global.resolver;

import static org.dinosaur.foodbowl.global.exception.ErrorStatus.JWT_MALFORMED;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtUser;
import org.dinosaur.foodbowl.global.exception.ErrorStatus;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class MemberIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Long.class)
                && parameter.hasParameterAnnotation(MemberId.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.isAuthenticated()) {
            throw new FoodbowlException(ErrorStatus.JWT_NOT_AUTHENTICATION);
        }
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        return parseToLong(jwtUser.getUsername());
    }

    private Long parseToLong(String memberId) {
        try {
            return Long.parseLong(memberId);
        } catch (NumberFormatException e) {
            throw new FoodbowlException(JWT_MALFORMED);
        }
    }
}
