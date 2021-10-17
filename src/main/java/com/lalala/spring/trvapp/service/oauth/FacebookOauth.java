package com.lalala.spring.trvapp.service.oauth;

import com.lalala.spring.trvapp.dto.UserResponse;
import com.lalala.spring.trvapp.entity.User;
import com.lalala.spring.trvapp.vo.oauth.OAuthResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Optional;
@Slf4j
@Component
@RequiredArgsConstructor
public class FacebookOauth implements SocialOauth{

    @Value("${server.protocol}://${server.out-address}/user/auth/facebook")
    private String redirectUrl;
    @Value("${external.auth.facebook.client.client_id}")
    private String clientId;
    @Value("${external.auth.facebook.client.client_secret}")
    private String clientSecret;
    @Value("${external.auth.facebook.client.access_token_url}")
    private String tokenBaseUrl;

    @Override
    public String getOauthRedirectURL() {
        return null;
    }
    @Override
    public Optional<OAuthResponseVO> requestAccessToken(UserResponse userResponse) {

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("code", userResponse.getCode());
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUrl);

        return getPostOAuthResponse(params, tokenBaseUrl);
    }

    @Override
    public Optional<OAuthResponseVO> refreshAccessToken(UserResponse userResponse) {
        return Optional.empty();
    }

    @Override
    public User getUserInfo(String idToken) {
        return null;
    }
}
