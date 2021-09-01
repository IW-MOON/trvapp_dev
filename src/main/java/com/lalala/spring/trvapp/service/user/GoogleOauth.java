package com.lalala.spring.trvapp.service.user;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.lalala.spring.trvapp.exception.ServerRuntimeException;
import com.lalala.spring.trvapp.helper.HttpClientUtils;
import com.lalala.spring.trvapp.model.OAuthResponse;
import com.lalala.spring.trvapp.model.ServiceResponse;
import com.lalala.spring.trvapp.model.User;
import com.lalala.spring.trvapp.type.SocialAuthType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

    @Value("${server.protocol}://${server.out-address}:${server.out-port}/user/auth/google")
    private String redirectUrl;
    @Value("${external.auth.google.client_id}")
    private String clientId;
    @Value("${external.auth.google.client_secret}")
    private String clientSecret;
    @Value("${external.auth.google.token_base_url}")
    private String tokenBaseUrl;


    private final HttpClientUtils httpClientUtils;

    @Override
    public String getOauthRedirectURL() {
        return null;
    }

    @Override
    public Optional<OAuthResponse> requestAccessToken(ServiceResponse serviceResponse) {

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("code", serviceResponse.getCode());
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUrl);
        params.add("grant_type", "authorization_code");

        return httpClientUtils.getPostOAuthResponse(params, tokenBaseUrl);
    }

    @Override
    public Optional<OAuthResponse> refreshAccessToken(ServiceResponse serviceResponse) {

        log.info(redirectUrl);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("refresh_token", serviceResponse.getRefreshToken());
        params.add("grant_type", "refresh_token");

        return httpClientUtils.getPostOAuthResponse(params, tokenBaseUrl);
    }

//    private Optional<OAuthResponse> getOAuthResponse(Map<String, Object> params) {
//        Optional<ResponseEntity<String>> optionalResponseEntity = httpClientUtils.doPostResponseEntity(params, tokenBaseUrl);
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//
//        return optionalResponseEntity.map(
//                responseEntity -> {
//                    try {
//                        if (responseEntity.getStatusCode() == HttpStatus.OK) {
//                            OAuthResponse oAuthResponse = mapper.readValue(responseEntity.getBody(), new TypeReference<OAuthResponse>() {
//                            });
//                            System.out.println("result = " + oAuthResponse);
//                            return Optional.ofNullable(oAuthResponse);
//                        }
//                    } catch (Exception e){
//                        throw new ServerRuntimeException();
//                    }
//                    throw new UnAuthorizedException();
//                }
//
//        ).orElseThrow(UnAuthorizedException::new);
//    }

    @Override
    public User getUserInfo(OAuthResponse oAuthResponse) {

        String idToken = oAuthResponse.getIdToken();
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
                    .build();

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            throw new ServerRuntimeException();
        }
        return googleUser;
    }

}
