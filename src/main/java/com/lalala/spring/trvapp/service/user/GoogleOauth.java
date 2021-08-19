package com.lalala.spring.trvapp.service.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.lalala.spring.trvapp.exception.ServerRuntimeException;
import com.lalala.spring.trvapp.exception.UnAuthorizedException;
import com.lalala.spring.trvapp.model.OAuthResponse;
import com.lalala.spring.trvapp.model.ServiceResponse;
import com.lalala.spring.trvapp.model.User;
import com.lalala.spring.trvapp.type.SocialAuthType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOauth implements SocialOauth {

    @Value("${server.protocol}://${server.out-address}:${server.port}/user/auth/google")
    private String redirectUrl;
    @Value("${external.auth.google.client_id}")
    private String clientId;
    @Value("${external.auth.google.client_secret}")
    private String clientSecret;
    @Value("${external.auth.google.token_base_url}")
    private String tokenBaseUrl;


    @Override
    public String getOauthRedirectURL() {
        return null;
    }

    @Override
    public Optional<OAuthResponse> requestAccessToken(ServiceResponse serviceResponse) {

        RestTemplate restTemplate = new RestTemplate();

        log.info(redirectUrl);
        Map<String, Object> params = new HashMap<>();
        params.put("code", serviceResponse.getCode());
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", redirectUrl);
        params.put("grant_type", "authorization_code");

        return getGoogleOAuthResponseResponseEntity(params);
    }

    @Override
    public Optional<OAuthResponse> refreshAccessToken(ServiceResponse serviceResponse) {

        log.info(redirectUrl);
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("refresh_token", serviceResponse.getRefreshToken());
        params.put("grant_type", "refresh_token");

        return getGoogleOAuthResponseResponseEntity(params);
    }

    @Override
    public User getUserInfo(String token) {

        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();
        User googleUser = null;
        String userId = null;
        String email = null;

        try {
            GoogleIdToken idToken = verifier.verify(token);
            GoogleIdToken.Payload payload = idToken.getPayload();

            log.debug(payload.toString());
            userId = payload.getSubject();
            email = payload.getEmail();

            log.debug(userId);

            googleUser = User.builder()
                    .socialAuthType(SocialAuthType.GOOGLE)
                    .socialUniqId(userId)
                    .email(email)
                    .build();

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            throw new ServerRuntimeException();
        }
        return googleUser;
    }

    private Optional<OAuthResponse> getGoogleOAuthResponseResponseEntity(Map<String, Object> params) {

        RestTemplate restTemplate = new RestTemplate();

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        OAuthResponse result = null;
        try {

            ResponseEntity<String> responseEntity =
                    restTemplate.postForEntity(tokenBaseUrl, params, String.class);

            System.out.println("responseEntity.getStatusCode() = " + responseEntity.getStatusCode());

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                result = mapper.readValue(responseEntity.getBody(), new TypeReference<OAuthResponse>() {
                });
                System.out.println("result = " + result);

            }

        } catch (Exception e) {
            throw new UnAuthorizedException();
        }
        return Optional.ofNullable(result);
    }
}
