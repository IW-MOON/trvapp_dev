package com.lalala.spring.trvapp.service.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.lalala.spring.trvapp.model.GoogleOAuthResponse;
import com.lalala.spring.trvapp.model.User;
import com.lalala.spring.trvapp.type.SocialLoginType;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOauth implements SocialOauth{

    @Value("${server.protocol}://${server.address}:${server.port}/user/auth/google" )
    private String redirectUrl;
    @Value("${external.auth.google.client_id}")
    private String clientId;
    @Value("${external.auth.google.client_secret}")
    private String clientSecret;
    @Value("${external.auth.google.token_base_url}")
    private String tokenBaseUrl;

    private final UserService userService;

    @Override
    public String getOauthRedirectURL() {
        return null;
    }

    @Override
    public ResponseEntity<GoogleOAuthResponse> requestAccessToken(String code){

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", redirectUrl);
        params.put("grant_type", "authorization_code");

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        System.out.println("redirect_uri = " + redirectUrl);
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(tokenBaseUrl, params, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            //return responseEntity.getBody();
            GoogleOAuthResponse result = null;
            try {
                result = mapper.readValue(responseEntity.getBody(), new TypeReference<GoogleOAuthResponse>() {
                });
                System.out.println("responseEntity = " + responseEntity.getBody());

                User googleUser = getUserInfo(result.getIdToken());
                log.info(googleUser.toString());
                 userService.saveUser(googleUser);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            System.out.println("result = " + result);
            return new ResponseEntity<GoogleOAuthResponse>(result, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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

            log.info(payload.toString());
            userId = payload.getSubject();
            email = payload.getEmail();

            log.info(userId);
            log.debug(email);
            System.out.println("userId = " + userId);

            googleUser = User.builder()
                    .socialLoginType(SocialLoginType.GOOGLE)
                    .socialUniqId(userId)
                    .email(email)
                    .build();

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleUser;
    }
}
