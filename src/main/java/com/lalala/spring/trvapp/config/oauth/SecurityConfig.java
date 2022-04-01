package com.lalala.spring.trvapp.config.oauth;

import com.lalala.spring.trvapp.repository.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import com.lalala.spring.trvapp.service.oauth.CustomOauth2UserService;
import com.lalala.spring.trvapp.service.oauth.OAuth2AuthenticationFailureHandler;
import com.lalala.spring.trvapp.service.oauth.OAuth2SuccessHandler;
import com.lalala.spring.trvapp.filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity           // Spring Security 활성화
@EnableGlobalMethodSecurity( // SecurityMethod 활성화
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomOauth2UserService customOAuth2UserService;

    @Autowired
    private OAuth2SuccessHandler successHandler;

    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    public static final String[] ALLOWED_URI_ANTMATCHERS = new String[]{"/oauth2/**", "/login/**", "/token/**", "/swagger-ui.html", "/swagger/**", "/swagger-resources/**", "/webjars/**", "/v2/api-docs/**"};
    public static final String[] ALLOWED_URI_PATTERN = new String[]{"/oauth2", "/login", "/token", "/swagger-ui", "/swagger", "/swagger-resources", "/webjars", "/v2/api-docs"};

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .cors().and()
                .httpBasic().disable()
                .cors().disable()
                .formLogin().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()// URL별 권한 권리
                    .antMatchers(ALLOWED_URI_ANTMATCHERS).permitAll()
                    //.antMatchers("/token/**").permitAll()
                    //.antMatchers("/user/join/**").permitAll()
                    //.anyRequest().permitAll()// anyRequest : 설정된 값들 이외 나머지 URL 나타냄, authenticated : 인증된 사용자
                    .anyRequest().authenticated()
                .and()
                    .oauth2Login()
                            .authorizationEndpoint()
                            .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                            .baseUri("/oauth2/authorization")
                            .and()
                            .redirectionEndpoint()
                            .baseUri("/login/oauth2/code/*")
                            .and()
                            .userInfoEndpoint()
                            .userService(customOAuth2UserService)
                            .and()
                            .successHandler(successHandler)
                            .failureHandler(oAuth2AuthenticationFailureHandler);

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }
}
