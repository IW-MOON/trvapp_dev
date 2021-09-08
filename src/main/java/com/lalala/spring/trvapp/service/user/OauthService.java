package com.lalala.spring.trvapp.service.user;

import com.lalala.spring.trvapp.model.OAuthResponse;
import com.lalala.spring.trvapp.model.UserResponse;
import com.lalala.spring.trvapp.entity.User;
import com.lalala.spring.trvapp.type.SocialAuthType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

    public Optional<OAuthResponse> requestAccessToken(SocialAuthType socialAuthType, UserResponse userResponse) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialAuthType);
        return socialOauth.requestAccessToken(userResponse);
    }

    public Optional<OAuthResponse> refreshAccessToken(SocialAuthType socialAuthType, UserResponse userResponse) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialAuthType);
        return socialOauth.refreshAccessToken(userResponse);
    }

    private SocialOauth findSocialOauthByType(SocialAuthType socialAuthType) {

        return socialOauthList.stream()
                .filter(x -> x.type() == socialAuthType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 SocialLoginType 입니다."));
    }

    public User getUserInfo(SocialAuthType socialAuthType, String idToken){

        SocialOauth socialOauth = this.findSocialOauthByType(socialAuthType);
        return socialOauth.getUserInfo(idToken);
    }

}
