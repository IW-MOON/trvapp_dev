package com.lalala.spring.trvapp.service.oauth;

import com.lalala.spring.trvapp.config.oauth.AuthConfig;
import com.lalala.spring.trvapp.dto.oauth.OAuth2Attribute;
import com.lalala.spring.trvapp.dto.oauth.OAuthUserResponse;
import com.lalala.spring.trvapp.dto.token.TokenResponse;
import com.lalala.spring.trvapp.entity.role.RoleType;
import com.lalala.spring.trvapp.helper.CookieUtils;
import com.lalala.spring.trvapp.repository.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import com.lalala.spring.trvapp.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        String targetUrl = determineTargetUrl(request, response, authentication);
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        OAuthUserResponse oAuthUserResponse = OAuthUserResponse.from(defaultOAuth2User);

        TokenResponse tokenResponse = tokenService.generateToken(oAuthUserResponse.getId(), oAuthUserResponse.getEmail(), Arrays.asList(RoleType.ROLE_TEMP));
        log.debug("tokenResponse : {}", tokenResponse);
        log.debug("targetUrl : {}", targetUrl);

        writeTokenResponse(response, tokenResponse);

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void writeTokenResponse(HttpServletResponse response, TokenResponse tokenResponse) {
        response.addHeader(AuthConfig.HEADER_NAME_TOKEN_AUTH, tokenResponse.getToken());
        response.addHeader(AuthConfig.HEADER_NAME_TOKEN_REFRESH, tokenResponse.getRefreshToken());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    //token을 생성하고 이를 포함한 프론트엔드로의 uri를 생성한다.
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        Optional<String> redirectUri = CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        return UriComponentsBuilder.fromUriString(targetUrl)
                .build().toUriString();
    }

    //인증정보 요청 내역을 쿠키에서 삭제한다.
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
