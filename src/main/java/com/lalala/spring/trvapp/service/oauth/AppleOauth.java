package com.lalala.spring.trvapp.service.oauth;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.lalala.spring.trvapp.dto.token.RefreshTokenRequest;
import com.lalala.spring.trvapp.dto.oauth.OAuthResponse;
import com.lalala.spring.trvapp.entity.user.User;
import com.lalala.spring.trvapp.exception.UnAuthorizedException;
import com.lalala.spring.trvapp.helper.HttpClientUtils;
import com.lalala.spring.trvapp.type.SocialAuthType;
import com.lalala.spring.trvapp.vo.oauth.ApplePublicKey;
import com.lalala.spring.trvapp.vo.oauth.ApplePublicKeys;
import com.lalala.spring.trvapp.vo.oauth.OAuthResponseVO;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class AppleOauth implements SocialOauth{

    @Value("${server.protocol}://${server.out-address}/user/auth/apple" )
    private String redirectUrl;
    @Value("${external.auth.apple.token_url}" )
    private String tokenBaseUrl;
    @Value("${external.auth.apple.public_key_url}" )
    private String publicKeyUrl;

    @Value("${token.expire_time.apple}")
    private long appleExpTime;

    @Value("${external.auth.apple.client_id}")
    private String clientId;
    @Value("${external.auth.apple.team_id}")
    private String teamId;
    @Value("${external.auth.apple.key_id}")
    private String keyId;
    @Value("${external.auth.apple.iss}")
    private String iss;

    @Value("${external.auth.apple.key_path}")
    private String keyPath;

    private final HttpClientUtils httpClientUtils;

    @Override
    public String getOauthRedirectURL() {
        return null;
    }

    @Override
    public OAuthResponseVO requestAccessToken(OAuthResponse OAuthResponse){

        String idToken = OAuthResponse.getIdToken();
        if(idToken == null){
            throw new IllegalArgumentException("IdToken is null");
        }
        String clientSecret = getAppleClientSecret(OAuthResponse.getIdToken());
        if(clientSecret == null){
            throw new UnAuthorizedException("ClientSecret Is Null!");
        }
        return validateAuthorizationGrantCode(clientSecret, OAuthResponse.getCode());
    }

    @Override
    public OAuthResponseVO refreshAccessToken(RefreshTokenRequest refreshTokenRequest) {

        return validateAnExistingRefreshToken(refreshTokenRequest.getClientSecret(), refreshTokenRequest.getRefreshToken());
    }

    @Override
    public User getUserInfo(String idToken, String accessToken) {

        User appleUser = null;

        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet payload = signedJWT.getJWTClaimsSet();
            String email = payload.getStringClaim("email");

            if (email == null)
                throw new UnAuthorizedException("Email Value Is Null");

            appleUser = User.builder()
                    .socialAuthType(SocialAuthType.APPLE)
                    .socialUniqId(email)
                    .email(email)
                    .lastLoginDtm(LocalDateTime.now())
                    .build();

        } catch (ParseException e1){
            throw new UnAuthorizedException();
        }
       return appleUser;
    }

    public String getAppleClientSecret(String idToken) {

        if (verifyIdentityToken(idToken)) {
            return createClientSecret();
        }
        return null;
    }

    /**
     * User??? Sign in with Apple ??????(https://appleid.apple.com/auth/authorize)?????? ???????????? id_token??? ????????? ?????? ??????
     * Apple Document URL ??? https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api/verifying_a_user
     *
     * @param idToken
     * @return boolean
     */
    public boolean verifyIdentityToken(String idToken) {

        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet payload = signedJWT.getJWTClaimsSet();
            log.info(payload.toString());

            // EXP
            Date currentTime = new Date(System.currentTimeMillis());
            log.info(currentTime.toString());
            if (!currentTime.before(payload.getExpirationTime())) {
                return false;
            }

            // RSA
            if (verifyPublicKey(signedJWT)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Apple Server?????? ?????? ?????? ????????? ?????? ??????
     *
     * @param signedJWT
     * @return
     */
    private boolean verifyPublicKey(SignedJWT signedJWT) {


        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        Optional<ResponseEntity<String>> result =  httpClientUtils.doGetResponseEntity(null, publicKeyUrl);
        return result.map(
            responseEntity -> {

                try {
                    if (responseEntity.getStatusCode() == HttpStatus.OK) {

                        ApplePublicKeys keys = mapper.readValue(responseEntity.getBody(),  new TypeReference<ApplePublicKeys>() {
                        });
                        for (ApplePublicKey key : keys.getKeys()) {
                            RSAKey rsaKey = (RSAKey) JWK.parse(mapper.writeValueAsString(key));
                            RSAPublicKey publicKey = rsaKey.toRSAPublicKey();
                            JWSVerifier verifier = new RSASSAVerifier(publicKey);
                            if (signedJWT.verify(verifier)) {
                                return true;
                            }
                        }
                    }

                } catch (JsonProcessingException | ParseException | JOSEException e) {
                    e.printStackTrace();
                }
                return false;
            }
        ).orElseThrow(UnAuthorizedException::new);
    }

    public String createClientSecret() {

        String clientSecret = null;
        try {
            Date now = new Date();

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(keyId).build();
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer(teamId)
                    .issueTime(now)
                    .expirationTime(new Date(now.getTime() + (appleExpTime)))
                    .audience(iss)
                    .subject(clientId).build();

            SignedJWT jwt = new SignedJWT(header, claimsSet);

            JWSSigner jwsSigner = new ECDSASigner((java.security.interfaces.ECPrivateKey)readPrivateKey());
            jwt.sign(jwsSigner);
            clientSecret = jwt.serialize();

        } catch (JOSEException e) {
            e.printStackTrace();
        }

        return clientSecret;
    }

    /**
     * ???????????? private key ??????
     *
     * @return Private Key
     */
    private PrivateKey readPrivateKey() {

        //Resource resource = new ClassPathResource("file : "+keyPath);
        byte[] content = null;
        PrivateKey privateKey = null;

        try {
            FileReader keyReader = new FileReader(keyPath);
            PemReader pemReader = new PemReader(keyReader);
            PemObject pemObject = pemReader.readPemObject();
            content = pemObject.getContent();

            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(content));

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return privateKey;
    }

    /**
     * ????????? code ?????? Apple Server??? ?????? ??????
     * Apple Document URL ??? https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens
     *
     * @return
     */
    public OAuthResponseVO validateAuthorizationGrantCode(String clientSecret, String code) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUrl);
        params.add("grant_type", "authorization_code");

        OAuthResponseVO oAuthResponseVO = getOAuthResponse(params, RequestMethod.POST, tokenBaseUrl);
        oAuthResponseVO.setClientSecret(clientSecret);

        return oAuthResponseVO;
    }

    /**
     * ????????? refresh_token ?????? Apple Server??? ?????? ??????
     * Apple Document URL ??? https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens
     *
     * @param clientSecret
     * @param refreshToken
     * @return
     */
    public OAuthResponseVO validateAnExistingRefreshToken(String clientSecret, String refreshToken) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);

        return getOAuthResponse(params, RequestMethod.POST, tokenBaseUrl);
    }



}
