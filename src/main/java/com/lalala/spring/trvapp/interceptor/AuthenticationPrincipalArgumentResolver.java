package com.lalala.spring.trvapp.interceptor;

import com.lalala.spring.trvapp.entity.token.Token;
import com.lalala.spring.trvapp.entity.user.User;
import com.lalala.spring.trvapp.repository.user.UserRepository;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;
    public AuthenticationPrincipalArgumentResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String credentials = AuthorizationExtractor.extract(httpServletRequest);

        if(httpServletRequest.getRequestURI().startsWith("/token/refresh")){
            return credentials;
        }
        Token token = new Token(credentials);
        String id = token.getId();
        User user = userRepository.findBySocialUniqId(id).orElse(null);
        if (user == null){
            return credentials;
        }
        return user;
    }
}
