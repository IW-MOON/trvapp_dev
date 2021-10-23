package com.lalala.spring.trvapp.service.oauth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.lalala.spring.trvapp.dto.UserResponse;
import com.lalala.spring.trvapp.entity.User;
import com.lalala.spring.trvapp.exception.ServerRuntimeException;
import com.lalala.spring.trvapp.exception.UnAuthorizedException;
import com.lalala.spring.trvapp.helper.HttpClientUtils;
import com.lalala.spring.trvapp.type.SocialAuthType;
import com.lalala.spring.trvapp.vo.oauth.OAuthResponseVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

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
    Optional<OAuthResponseVO> requestAccessToken(UserResponse userResponse);
    Optional<OAuthResponseVO> refreshAccessToken(UserResponse userResponse);

    User getUserInfo(String idToken, String accessToken);

    default SocialAuthType type() {
        if (this instanceof GoogleOauth) {
            return SocialAuthType.GOOGLE;
        } else if (this instanceof AppleOauth) {
            return SocialAuthType.APPLE;
        } else if (this instanceof FacebookOauth) {
            return SocialAuthType.FACEBOOK;
        }
//        } else if (this instanceof NaverOauth) {
//            return SocialLoginType.NAVER;
//        } else if (this instanceof KakaoOauth) {
//            return SocialLoginType.KAKAO;
//        }
        else {
            return null;
        }
    }

    default Optional<OAuthResponseVO> getOAuthResponse(MultiValueMap<String, Object> params, RequestMethod requestMethod, String url) {

        HttpClientUtils httpClientUtils = new HttpClientUtils();
        Optional<ResponseEntity<String>> optionalResponseEntity;

        if(requestMethod == RequestMethod.GET){
            optionalResponseEntity = httpClientUtils.doGetResponseEntity(params.toSingleValueMap(), url);
        } else {
            optionalResponseEntity = httpClientUtils.doPostResponseEntity(params, url);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return optionalResponseEntity.map(
                responseEntity -> {
                    try {
                        if (responseEntity.getStatusCode() == HttpStatus.OK) {
                            OAuthResponseVO oAuthResponse = mapper.readValue(responseEntity.getBody(), new TypeReference<OAuthResponseVO>() {
                            });
                            return Optional.ofNullable(oAuthResponse);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        throw new ServerRuntimeException();
                    }
                    throw new UnAuthorizedException();
                }

        ).orElseThrow(UnAuthorizedException::new);
    }
}
