package org.dinosaur.foodbowl.global.presentation;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtUser;
import org.dinosaur.foodbowl.domain.auth.exception.AuthExceptionType;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.exception.MemberExceptionType;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.dinosaur.foodbowl.global.exception.AuthenticationException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
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
public class MemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Member.class) && parameter.hasParameterAnnotation(Auth.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.isAuthenticated()) {
            throw new AuthenticationException(AuthExceptionType.NOT_AUTHENTICATION);
        }
        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        Long memberId = parseToLong(jwtUser.getUsername());
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));
    }

    private Long parseToLong(String memberId) {
        try {
            return Long.parseLong(memberId);
        } catch (NumberFormatException e) {
            throw new AuthenticationException(AuthExceptionType.MALFORMED_JWT);
        }
    }
}
