package com.lalala.spring.trvapp.service.user;

import com.lalala.spring.trvapp.entity.Auth;
import com.lalala.spring.trvapp.exception.BadRequestException;
import com.lalala.spring.trvapp.exception.UnAuthorizedException;
import com.lalala.spring.trvapp.helper.JwtTokenProvider;
import com.lalala.spring.trvapp.model.OAuthResponse;
import com.lalala.spring.trvapp.model.UserResponse;
import com.lalala.spring.trvapp.entity.User;
import com.lalala.spring.trvapp.entity.UserToken;
import com.lalala.spring.trvapp.repository.AuthRepository;
import com.lalala.spring.trvapp.repository.UserRepository;
import com.lalala.spring.trvapp.repository.UserTokenRepository;
import com.lalala.spring.trvapp.type.SocialAuthType;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final AuthRepository authRepository;
    private final OauthService oauthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final WordsGenerate wordsGenerate;


    public ResponseEntity<UserResponse> auth(SocialAuthType socialAuthType, UserResponse userResponse){
        if(userResponse.getCode() == null){
            throw new UnAuthorizedException();
        }

        Optional<OAuthResponse> optOAuthResponse = oauthService.requestAccessToken(socialAuthType, userResponse);
        return optOAuthResponse.map(oAuthResponse -> {

            String jwt = jwtTokenProvider.encodeJwtToken(socialAuthType, oAuthResponse.getAccessToken(), oAuthResponse.getRefreshToken());
            String idToken = oAuthResponse.getIdToken();

            User user = oauthService.getUserInfo(socialAuthType, idToken);

            Optional<User> optFindUser =
                    userRepository.findBySocialUniqId(user.getSocialUniqId());

            if(optFindUser.isPresent()){

                User findUser = optFindUser.get();
                findUser.updateLastLoginDtm();

                Optional<UserToken> optUserToken = userTokenRepository.findByUser(findUser);
                optUserToken.ifPresentOrElse(
                        findUserToken -> {
                            if(!findUserToken.getRefreshToken().equals(oAuthResponse.getRefreshToken()))
                                findUserToken.updateRefreshToken(oAuthResponse.getRefreshToken());
                        },
                        () -> saveRefreshToken(findUser, oAuthResponse.getRefreshToken())
                );
                return new ResponseEntity<UserResponse>(
                        UserResponse.builder()
                                .token(jwt)
                                .accessToken(oAuthResponse.getAccessToken())
                                .refreshToken(oAuthResponse.getRefreshToken())
                                .clientSecret(oAuthResponse.getClientSecret())
                                .isJoined(true)
                                .build(), HttpStatus.OK);

            } else {

                Auth auth = Auth.builder()
                        .socialAuthType(socialAuthType)
                        .token(jwt)
                        .joinYn("N").build();
                long idx = authRepository.save(auth).getAuthIdx();

                return new ResponseEntity<UserResponse>(
                        UserResponse.builder()
                                .token(jwt)
                                .idToken(idToken)
                                .clientSecret(oAuthResponse.getClientSecret())
                                .isJoined(false)
                                .idx(idx)
                                .build(), HttpStatus.OK);

            }
        }).orElseThrow(UnAuthorizedException::new);

    }

    public ResponseEntity<UserResponse> join(SocialAuthType socialAuthType, UserResponse userResponse) throws UnAuthorizedException {

        long authIdx = userResponse.getIdx();
        String token = userResponse.getToken();
        String idToken = userResponse.getIdToken();

        if(token == null || idToken == null){
            throw new BadRequestException();
        }
        if(!jwtTokenProvider.validateToken(token)){
            throw new UnAuthorizedException();
        }

        Optional<Auth> optAuth = authRepository.findByAuthIdx(authIdx);

        optAuth.ifPresentOrElse(
            auth -> {
                if(!auth.getToken().equals(token)){
                    throw new UnAuthorizedException();
                }
                auth.updateJoinYn();
            }, UnAuthorizedException::new
        );

        String accessToken = jwtTokenProvider.getClaim(token, "access_token");
        String refreshToken = jwtTokenProvider.getClaim(token, "refresh_token");

        User user = oauthService.getUserInfo(socialAuthType, idToken);
        String nickName = wordsGenerate.generateNickName();
        user.setNickName(nickName);
        log.info(user.toString());

        User saveUser = userRepository.save(user);
        saveRefreshToken(saveUser, userResponse.getRefreshToken());

        return new ResponseEntity<UserResponse>(
                UserResponse.builder()
                        .token(token)
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .isJoined(true)
                        .build(), HttpStatus.OK);

    }

    public ResponseEntity<UserResponse> refreshToken(SocialAuthType socialAuthType, UserResponse userResponse){

        if(socialAuthType == SocialAuthType.APPLE){
            if(userResponse.getClientSecret() == null){
                throw new BadRequestException();
            }
        }
        String refreshToken = userResponse.getRefreshToken();
        if(refreshToken == null){
            throw new BadRequestException();
        }

        Optional<OAuthResponse> optOAuthResponse = oauthService.refreshAccessToken(socialAuthType, userResponse);
        return optOAuthResponse.map(oAuthResponse -> {

            Optional<UserToken> optUserToken =
                    userTokenRepository.findByRefreshToken(refreshToken);

            optUserToken.ifPresentOrElse(
                    findUserToken -> {
                        if(!findUserToken.getRefreshToken().equals(oAuthResponse.getRefreshToken()))
                            findUserToken.updateRefreshToken(oAuthResponse.getRefreshToken());
                    }, UnAuthorizedException::new
            );

            String jwt = jwtTokenProvider.encodeJwtToken(socialAuthType, oAuthResponse.getAccessToken(), userResponse.getRefreshToken());
            return new ResponseEntity<UserResponse>(
                    UserResponse.builder()
                            .token(jwt)
                            .accessToken(oAuthResponse.getAccessToken())
                            .refreshToken(userResponse.getRefreshToken())
                            .build(), HttpStatus.OK);

        }).orElseThrow(UnAuthorizedException::new);

    }

    private void saveRefreshToken(User user, String refreshToken){

        if(refreshToken != null){
            UserToken userToken = UserToken.builder()
                    .user(user)
                    .refreshToken(refreshToken)
                    .creationDtm(LocalDateTime.now()).build();
            userTokenRepository.save(userToken);
        }
    }

    public void processEndpoint(String payload) {

        try {
            SignedJWT signedJWT = SignedJWT.parse(payload);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            Map<String, Object> eventMap = claimsSet.getClaims();
            log.info("event_type" + eventMap.get("type"));

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


}
