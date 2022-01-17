package com.lalala.spring.trvapp.service.oauth;

import com.lalala.spring.trvapp.dto.token.RefreshTokenRequest;
import com.lalala.spring.trvapp.dto.oauth.OAuthResponse;
import com.lalala.spring.trvapp.entity.user.User;
import com.lalala.spring.trvapp.type.SocialAuthType;
import com.lalala.spring.trvapp.vo.oauth.OAuthResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class FacebookOauth implements SocialOauth{

    //@Value("${server.protocol}://${server.out-address}/user/auth/facebook")
    private String redirectUrl;
    //@Value("${external.auth.facebook.client.client_id}")
    private String clientId;
    //@Value("${external.auth.facebook.client.client_secret}")
    private String clientSecret;
    //@Value("${external.auth.facebook.client.access_token_url}")
    private String tokenBaseUrl;

    //@Value("${external.auth.facebook.resource.user_info_uri}")
    private String userInfoUrl;

    @Override
    public String getOauthRedirectURL() {
        return null;
    }
    @Override
    public OAuthResponseVO requestAccessToken(OAuthResponse OAuthResponse) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", OAuthResponse.getCode());
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUrl);

        return getOAuthResponse(params, RequestMethod.POST, tokenBaseUrl);
    }

    @Override
    public OAuthResponseVO refreshAccessToken(RefreshTokenRequest refreshTokenRequest) {
        return null;
    }

    @Override
    public User getUserInfo(String idToken, String accessToken) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("access_token", accessToken);

        OAuthResponseVO oAuthResponseVO = getOAuthResponse(params, RequestMethod.GET, userInfoUrl);
        User facebookUser = null;

        facebookUser = User.builder()
                .socialAuthType(SocialAuthType.FACEBOOK)
                .socialUniqId(oAuthResponseVO.getId())
                .email(oAuthResponseVO.getEmail())
                .lastLoginDtm(LocalDateTime.now())
                .build();

        return facebookUser;
    }
}
