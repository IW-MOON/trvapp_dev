package com.lalala.spring.trvapp.service.oauth;

import com.lalala.spring.trvapp.dto.token.RefreshTokenRequest;
import com.lalala.spring.trvapp.dto.oauth.OAuthResponse;
import com.lalala.spring.trvapp.entity.user.User;
import com.lalala.spring.trvapp.type.SocialAuthType;
import com.lalala.spring.trvapp.vo.oauth.OAuthResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final List<SocialOauth> socialOauthList;
    private final HttpServletResponse response;


    public void request(SocialAuthType socialAuthType) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialAuthType);
        String redirectURL = socialOauth.getOauthRedirectURL();
        try {
            response.sendRedirect(redirectURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OAuthResponseVO requestAccessToken(SocialAuthType socialAuthType, OAuthResponse OAuthResponse) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialAuthType);
        return socialOauth.requestAccessToken(OAuthResponse);
    }

    public OAuthResponseVO refreshAccessToken(SocialAuthType socialAuthType, RefreshTokenRequest refreshTokenRequest) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialAuthType);
        return socialOauth.refreshAccessToken(refreshTokenRequest);
    }

    private SocialOauth findSocialOauthByType(SocialAuthType socialAuthType) {

        return socialOauthList.stream()
                .filter(x -> x.type() == socialAuthType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 SocialLoginType 입니다."));
    }

    public User getUserInfo(SocialAuthType socialAuthType, String idToken, String accessToken){

        SocialOauth socialOauth = this.findSocialOauthByType(socialAuthType);
        return socialOauth.getUserInfo(idToken, accessToken);
    }

}
