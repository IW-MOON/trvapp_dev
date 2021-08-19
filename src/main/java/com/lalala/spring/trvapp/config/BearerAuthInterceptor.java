package com.lalala.spring.trvapp.config;

import com.lalala.spring.trvapp.exception.ForbiddenException;
import com.lalala.spring.trvapp.exception.UnAuthorizedException;
import com.lalala.spring.trvapp.helper.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@Component
@AllArgsConstructor
public class BearerAuthInterceptor implements HandlerInterceptor {

    public static final String AUTHORIZATION = "Authorization";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) {
        System.out.println(">>> interceptor.preHandle 호출");
        String token = extract(request, "Bearer");
        if (StringUtils.isEmpty(token)) {
            throw new UnAuthorizedException();
        }

        if (!jwtTokenProvider.validateToken(token)) {
            throw new ForbiddenException();
        }

        String accessToken = jwtTokenProvider.getClaim(token, "access_token");
        String refreshToken = jwtTokenProvider.getClaim(token, "refresh_token");
        request.setAttribute("access_token", accessToken);
        request.setAttribute("refresh_token", refreshToken);

        //return super.preHandle(request, response, handler);;
        return true;
    }

    public String extract(HttpServletRequest request, String type) {
        Enumeration<String> headers = request.getHeaders(AUTHORIZATION);
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (value.toLowerCase().startsWith(type.toLowerCase())) {
                return value.substring(type.length()).trim();
            }
        }
        return Strings.EMPTY;
    }

}
