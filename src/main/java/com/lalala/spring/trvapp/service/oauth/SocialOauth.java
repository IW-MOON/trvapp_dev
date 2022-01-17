package com.lalala.spring.trvapp.service.oauth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.lalala.spring.trvapp.dto.token.RefreshTokenRequest;
import com.lalala.spring.trvapp.dto.oauth.OAuthResponse;
import com.lalala.spring.trvapp.entity.user.User;
import com.lalala.spring.trvapp.exception.ServerRuntimeException;
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
     * 사용자로부터 로그인 요청을 받아 Social Login Server 인증용 code 요청
     */
    String getOauthRedirectURL();

    /**
     * API Server로부터 받은 code를 활용하여 사용자 인증 정보 요청
     * @param code API Server 에서 받아온 code
     * @return API 서버로 부터 응답받은 Json 형태의 결과를 string으로
     */

    OAuthResponseVO requestAccessToken(OAuthResponse OAuthResponse);
    OAuthResponseVO refreshAccessToken(RefreshTokenRequest refreshTokenRequest);

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

    default OAuthResponseVO getOAuthResponse(MultiValueMap<String, String> params, RequestMethod requestMethod, String url)  {

        HttpClientUtils httpClientUtils = new HttpClientUtils();
        Optional<ResponseEntity<String>> optionalResponseEntity = Optional.empty();

        if(requestMethod == RequestMethod.GET){
            optionalResponseEntity = httpClientUtils.doGetResponseEntity(params, url);
        }
        if(requestMethod == RequestMethod.POST){
            optionalResponseEntity = httpClientUtils.doPostResponseEntity(params, url);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        ResponseEntity<String> responseEntity = optionalResponseEntity.orElseThrow(IllegalArgumentException::new);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            try {
                return mapper.readValue(responseEntity.getBody(), new TypeReference<OAuthResponseVO>() {
                });
            } catch (JsonProcessingException e) {
                throw new ServerRuntimeException();
            }
        }
        throw new ServerRuntimeException();
    }
}
