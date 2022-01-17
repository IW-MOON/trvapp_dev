package com.lalala.spring.trvapp.filter;

import com.lalala.spring.trvapp.entity.token.Token;
import com.lalala.spring.trvapp.interceptor.AuthorizationExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {


//    private Authentication getAuthentication(OAuthUserResponse member) {
//        return new UsernamePasswordAuthenticationToken(member, "",
//                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Token token = new Token(AuthorizationExtractor.extract(request));

        if(!token.verifyToken()){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        log.info("token : {}", token);
        Authentication authentication = token.getAuthentication();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request , response);
    }
}
