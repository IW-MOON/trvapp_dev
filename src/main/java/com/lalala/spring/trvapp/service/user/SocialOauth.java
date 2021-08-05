package com.lalala.spring.trvapp.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lalala.spring.trvapp.model.GoogleOAuthResponse;
import com.lalala.spring.trvapp.model.User;
import com.lalala.spring.trvapp.type.SocialLoginType;
import org.springframework.http.ResponseEntity;

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
    ResponseEntity<GoogleOAuthResponse> requestAccessToken(String code);

    User getUserInfo(String token);

    default SocialLoginType type() {
        if (this instanceof GoogleOauth) {
            return SocialLoginType.GOOGLE;
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
