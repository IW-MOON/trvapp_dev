package com.lalala.spring.trvapp.service.oauth;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.lalala.spring.trvapp.exception.ServerRuntimeException;
import com.lalala.spring.trvapp.dto.UserResponse;
import com.lalala.spring.trvapp.entity.User;
import com.lalala.spring.trvapp.type.SocialAuthType;
import com.lalala.spring.trvapp.vo.oauth.OAuthResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOauth implements SocialOauth {

    @Value("${server.protocol}://${server.out-address}:${server.out-port}/user/auth/google")
    private String redirectUrl;
    @Value("${external.auth.google.client_id}")
    private String clientId;
    @Value("${external.auth.google.client_secret}")
    private String clientSecret;
    @Value("${external.auth.google.token_url}")
    private String tokenBaseUrl;


    @Override
    public String getOauthRedirectURL() {
        return null;
    }

    @Override
    public Optional<OAuthResponseVO> requestAccessToken(UserResponse userResponse) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", userResponse.getCode());
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUrl);
        params.add("grant_type", "authorization_code");

        return getOAuthResponse(params, RequestMethod.POST, tokenBaseUrl);
    }

    @Override
    public Optional<OAuthResponseVO> refreshAccessToken(UserResponse userResponse) {

        log.info(redirectUrl);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("refresh_token", userResponse.getRefreshToken());
        params.add("grant_type", "refresh_token");

        return getOAuthResponse(params, RequestMethod.POST, tokenBaseUrl);
    }

    @Override
    public User getUserInfo(String idToken, String accessToken) {

        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();
        User googleUser = null;
        String userId = null;
        String email = null;

        try {
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            GoogleIdToken.Payload payload = googleIdToken.getPayload();

            log.debug(payload.toString());
            userId = payload.getSubject();
            email = payload.getEmail();

            log.debug(userId);

            googleUser = User.builder()
                    .socialAuthType(SocialAuthType.GOOGLE)
                    .socialUniqId(userId)
                    .email(email)
                    .lastLoginDtm(LocalDateTime.now())
                    .build();

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            throw new ServerRuntimeException();
        }
        return googleUser;
    }

}
