package com.lalala.spring.trvapp.service.user;

import com.lalala.spring.trvapp.model.OAuthResponse;
import com.lalala.spring.trvapp.model.ServiceResponse;
import com.lalala.spring.trvapp.model.User;
import com.lalala.spring.trvapp.type.SocialAuthType;

import java.util.Optional;

public interface SocialOauth {

    /**
     * 각 Social Login 페이지로 Redirect 처리할 URL Build
     * 사용자로부터 로그인 요청을 받아 Social Login Server 인증용 code 요
     */
    String getOauthRedirectURL();

    /**
     * API Server로부터 받은 code를 활용하여 사용자 인증 정보 요청
     * @param code API Server 에서 받아온 code
     * @return API 서버로 부터 응답받은 Json 형태의 결과를 string으로 반
     */
    Optional<OAuthResponse> requestAccessToken(ServiceResponse serviceResponse);
    Optional<OAuthResponse> refreshAccessToken(ServiceResponse serviceResponse);

    User getUserInfo(String token);

    default SocialAuthType type() {
        if (this instanceof GoogleOauth) {
            return SocialAuthType.GOOGLE;
        } else if (this instanceof AppleOauth) {
            return SocialAuthType.APPLE;
        }
//        else if (this instanceof FacebookOauth) {
//            return SocialLoginType.FACEBOOK;
//        } else if (this instanceof NaverOauth) {
//            return SocialLoginType.NAVER;
//        } else if (this instanceof KakaoOauth) {
//            return SocialLoginType.KAKAO;
//        }
        else {
            return null;
        }
    }
}
