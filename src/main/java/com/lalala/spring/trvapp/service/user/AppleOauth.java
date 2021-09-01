package com.lalala.spring.trvapp.service.user;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.lalala.spring.trvapp.exception.UnAuthorizedException;
import com.lalala.spring.trvapp.helper.HttpClientUtils;
import com.lalala.spring.trvapp.model.*;
import com.lalala.spring.trvapp.type.SocialAuthType;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.crypto.interfaces.PBEKey;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleOauth implements SocialOauth{

    @Value("${server.protocol}://${server.address}/user/auth/apple" )
    private String redirectUrl;
    @Value("${external.auth.apple.token_url}" )
    private String tokenUrl;
    @Value("${external.auth.apple.public_key_url}" )
    private String publicKeyUrl;

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
    public Optional<OAuthResponse> requestAccessToken(ServiceResponse serviceResponse){

        String idToken = serviceResponse.getIdToken();
        if(idToken == null){
            throw new UnAuthorizedException();
        }
        String clientSecret = getAppleClientSecret(serviceResponse.getIdToken());
        if(clientSecret == null){
            throw new UnAuthorizedException();
        }
        log.debug(clientSecret);

        return validateAuthorizationGrantCode(clientSecret, serviceResponse.getCode());
    }

    @Override
    public Optional<OAuthResponse> refreshAccessToken(ServiceResponse serviceResponse) {

        if(serviceResponse.getClientSecret() == null){
            throw new UnAuthorizedException();
        }
        if(serviceResponse.getRefreshToken() == null){
            throw new UnAuthorizedException();
        }
        return validateAnExistingRefreshToken(serviceResponse.getClientSecret(), serviceResponse.getRefreshToken());
    }

    @Override
    public User getUserInfo(OAuthResponse oAuthResponse) {

        Object user = oAuthResponse.getUser();
        
//        User appleUser = User.builder()
//                .socialAuthType(SocialAuthType.APPLE)
//                .email(oAuthResponse.getUser().g)


       return null;
    }

    public String getAppleClientSecret(String idToken) {

        if (verifyIdentityToken(idToken)) {
            return createClientSecret();
        }
        return null;
    }

    /**
     * User가 Sign in with Apple 요청(https://appleid.apple.com/auth/authorize)으로 전달받은 id_token을 이용한 최초 검증
     * Apple Document URL ‣ https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api/verifying_a_user
     *
     * @param idToken
     * @return boolean
     */
    public boolean verifyIdentityToken(String idToken) {

        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet payload = signedJWT.getJWTClaimsSet();
            log.info(payload.toString());
            log.info(payload.getJWTID());
            log.info(payload.getExpirationTime().toString());

            log.info(payload.getStringClaim("email"));
            // EXP
            Date currentTime = new Date(System.currentTimeMillis());
            log.info(currentTime.toString());
            if (!currentTime.before(payload.getExpirationTime())) {
                return false;
            }

            // NONCE(Test value), ISS, AUD
//            if (!"20B20D-0S8-1K8".equals(payload.getClaim("nonce")) || !IS.equals(payload.getIssuer()) || !AUD.equals(payload.getAudience().get(0))) {
//                return false;
//            }

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
     * Apple Server에서 공개 키를 받아서 서명 확인
     *
     * @param signedJWT
     * @return
     */
    private boolean verifyPublicKey(SignedJWT signedJWT) {


        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        Map<String, Object> map = new LinkedHashMap<>();

        Optional<ResponseEntity<String>> result =  httpClientUtils.doGetResponseEntity(map, publicKeyUrl);
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
                    .expirationTime(new Date(now.getTime() + 3600000))
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
     * 파일에서 private key 획득
     *
     * @return Private Key
     */
    private PrivateKey readPrivateKey() {

        Resource resource = new ClassPathResource("file : "+keyPath);
        byte[] content = null;
        PrivateKey privateKey = null;

        try {
            FileReader keyReader = new FileReader(resource.getURI().getPath());
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
     * 유효한 code 인지 Apple Server에 확인 요청
     * Apple Document URL ‣ https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens
     *
     * @return
     */
    public Optional<OAuthResponse> validateAuthorizationGrantCode(String clientSecret, String code) {


        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", redirectUrl);
        params.put("grant_type", "authorization_code");

        return httpClientUtils.getPostOAuthResponse(params, tokenUrl);
    }

    /**
     * 유효한 refresh_token 인지 Apple Server에 확인 요청
     * Apple Document URL ‣ https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens
     *
     * @param clientSecret
     * @param refreshToken
     * @return
     */
    public Optional<OAuthResponse> validateAnExistingRefreshToken(String clientSecret, String refreshToken) {

        Map<String, Object> params = new HashMap<>();

        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", refreshToken);

        return httpClientUtils.getPostOAuthResponse(params, tokenUrl);
    }


}
