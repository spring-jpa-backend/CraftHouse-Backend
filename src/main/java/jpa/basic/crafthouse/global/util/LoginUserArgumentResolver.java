package jpa.basic.crafthouse.global.util;

import jpa.basic.crafthouse.global.exception.ErrorCode;
import jpa.basic.crafthouse.member.exception.MemberException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        boolean hasAnnotation   = parameter.hasParameterAnnotation(LoginUser.class);
        boolean hasLoginUserType = LoginUserInfoDto.class.isAssignableFrom(parameter.getParameterType());
        return hasAnnotation && hasLoginUserType;
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                  @Nullable ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest,
                                  @Nullable WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || authentication.getPrincipal().equals("anonymousUser")
                || !(authentication.getPrincipal() instanceof LoginUserInfoDto)) {
            throw new MemberException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        return authentication.getPrincipal();
    }
}