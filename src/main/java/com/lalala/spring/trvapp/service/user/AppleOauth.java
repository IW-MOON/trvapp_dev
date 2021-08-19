package com.lalala.spring.trvapp.service.user;


import com.lalala.spring.trvapp.model.OAuthResponse;
import com.lalala.spring.trvapp.model.ServiceResponse;
import com.lalala.spring.trvapp.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    //private final UserService userService;

    @Override
    public String getOauthRedirectURL() {
        return null;
    }

    @Override
    public Optional<OAuthResponse> requestAccessToken(ServiceResponse serviceResponse){

//        String code = serviceResponse.getCode();
//        String clientSecret =

        return Optional.empty();
    }

    @Override
    public Optional<OAuthResponse> refreshAccessToken(ServiceResponse serviceResponse) {
        return Optional.empty();
    }

    @Override
    public User getUserInfo(String token) {

       return null;
    }



    public String getAppleClientSecret(String id_token) {

//        if (appleUtils.verifyIdentityToken(id_token)) {
//            return appleUtils.createClientSecret();
//        }

        return null;
    }

    /**
     * User가 Sign in with Apple 요청(https://appleid.apple.com/auth/authorize)으로 전달받은 id_token을 이용한 최초 검증
     * Apple Document URL ‣ https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api/verifying_a_user
     *
     * @param id_token
     * @return boolean
     */
    public boolean verifyIdentityToken(String id_token) {

//        try {
//            SignedJW signedJWT = SignedJWT.parse(id_token);
//            ReadOnlyJWTClaimsSet payload = signedJWT.getJWTClaimsSet();
//
//            // EXP
//            Date currentTime = new Date(System.currentTimeMillis());
//            if (!currentTime.before(payload.getExpirationTime())) {
//                return false;
//            }
//
//            // NONCE(Test value), ISS, AUD
//            if (!"20B20D-0S8-1K8".equals(payload.getClaim("nonce")) || !ISS.equals(payload.getIssuer()) || !AUD.equals(payload.getAudience().get(0))) {
//                return false;
//            }
//
//            // RSA
//            if (verifyPublicKey(signedJWT)) {
//                return true;
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        return false;
    }

}
