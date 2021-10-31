package com.lalala.spring.trvapp.service.user;

import com.lalala.spring.trvapp.entity.Auth;
import com.lalala.spring.trvapp.exception.BadRequestException;
import com.lalala.spring.trvapp.exception.UnAuthorizedException;
import com.lalala.spring.trvapp.helper.JwtTokenProvider;
import com.lalala.spring.trvapp.dto.UserResponse;
import com.lalala.spring.trvapp.entity.User;
import com.lalala.spring.trvapp.entity.UserToken;
import com.lalala.spring.trvapp.helper.WordsGenerate;
import com.lalala.spring.trvapp.repository.AuthRepository;
import com.lalala.spring.trvapp.repository.UserRepository;
import com.lalala.spring.trvapp.repository.UserTokenRepository;
import com.lalala.spring.trvapp.service.nickname.NicknameService;
import com.lalala.spring.trvapp.service.oauth.OauthService;
import com.lalala.spring.trvapp.type.SocialAuthType;
import com.lalala.spring.trvapp.vo.oauth.OAuthResponseVO;
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
    private final NicknameService nicknameService;


    public ResponseEntity<UserResponse> auth(SocialAuthType socialAuthType, UserResponse userResponse){
        if(userResponse.getCode() == null){
            throw new UnAuthorizedException();
        }

        Optional<OAuthResponseVO> optOAuthResponseVO = oauthService.requestAccessToken(socialAuthType, userResponse);
        return optOAuthResponseVO.map(oAuthResponseVO -> {

            System.out.println("oAuthResponse = " + oAuthResponseVO);
            String jwt = jwtTokenProvider.encodeJwtToken(socialAuthType, oAuthResponseVO);
            String idToken = oAuthResponseVO.getIdToken();

            User user = oauthService.getUserInfo(socialAuthType, idToken, oAuthResponseVO.getAccessToken());

            Optional<User> optFindUser =
                    userRepository.findBySocialUniqId(user.getSocialUniqId());

            if(optFindUser.isPresent()){

                User findUser = optFindUser.get();

                this.userLogin(optFindUser, oAuthResponseVO.getRefreshToken());
                return new ResponseEntity<UserResponse>(
                        UserResponse.builder()
                                .token(jwt)
                                .accessToken(oAuthResponseVO.getAccessToken())
                                .refreshToken(oAuthResponseVO.getRefreshToken())
                                .clientSecret(oAuthResponseVO.getClientSecret())
                                .isJoined(true)
                                .build(), HttpStatus.OK);

            } else {

                long idx = this.saveAuth(socialAuthType, jwt);
                return new ResponseEntity<UserResponse>(
                        UserResponse.builder()
                                .token(jwt)
                                .idToken(idToken)
                                .clientSecret(oAuthResponseVO.getClientSecret())
                                .isJoined(false)
                                .idx(idx)
                                .build(), HttpStatus.OK);

            }
        }).orElseThrow(UnAuthorizedException::new);

    }

    private void userLogin(Optional<User> user, String newRefreshToken) {

        User findUser = user.get();
        findUser.updateLastLoginDtm();


        Optional<UserToken> optUserToken = userTokenRepository.findByUser(findUser);
        optUserToken.ifPresentOrElse(
                findUserToken -> {
                    if(!findUserToken.getRefreshToken().equals(newRefreshToken))
                        findUserToken.updateRefreshToken(newRefreshToken);
                },
                () -> saveRefreshToken(findUser, newRefreshToken)
        );
    }

    private long saveAuth(SocialAuthType socialAuthType, String jwt) {

        Auth auth = Auth.builder()
                .socialAuthType(socialAuthType)
                .token(jwt)
                .joinYn("N").build();
        return authRepository.save(auth).getAuthIdx();
    }

    public ResponseEntity<UserResponse> join(SocialAuthType socialAuthType, UserResponse userResponse) throws UnAuthorizedException {

        long authIdx = userResponse.getIdx();
        String token = userResponse.getToken();
        String idToken = userResponse.getIdToken();

        if(token == null){
            throw new BadRequestException();
        }
        if(socialAuthType != SocialAuthType.FACEBOOK && idToken == null){
            throw new BadRequestException();
        }

        if(!jwtTokenProvider.isValidateToken(token)){
            throw new UnAuthorizedException();
        }
        this.checkPrevAuth(userResponse);

        String accessToken = jwtTokenProvider.getClaim(token, "access_token");
        String refreshToken =
                socialAuthType == SocialAuthType.FACEBOOK ? null : jwtTokenProvider.getClaim(token, "refresh_token");

        userResponse.setAccessToken(accessToken);
        this.saveUser(socialAuthType, userResponse);

        return new ResponseEntity<UserResponse>(
                UserResponse.builder()
                        .token(token)
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .isJoined(true)
                        .build(), HttpStatus.OK);

    }

    private void checkPrevAuth(UserResponse userResponse) {

        long authIdx = userResponse.getIdx();
        String token = userResponse.getToken();

        Optional<Auth> optAuth = authRepository.findByAuthIdx(authIdx);
        optAuth.ifPresentOrElse(
                auth -> {
                    if(!auth.getToken().equals(token)){
                        throw new UnAuthorizedException();
                    }
                    auth.updateJoinYn();
                }, UnAuthorizedException::new
        );
    }

    private void saveUser(SocialAuthType socialAuthType, UserResponse userResponse) {

        User user = oauthService.getUserInfo(socialAuthType, userResponse.getIdToken(), userResponse.getAccessToken());
        String nickName = nicknameService.generateNickName();
        user.setNickName(nickName);
        log.info(user.toString());

        User saveUser = userRepository.save(user);
        saveRefreshToken(saveUser, userResponse.getRefreshToken());
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

        Optional<OAuthResponseVO> optOAuthResponseVO = oauthService.refreshAccessToken(socialAuthType, userResponse);
        return optOAuthResponseVO.map(oAuthResponse -> {

            this.updateRefreshToken(userResponse, oAuthResponse);
            String jwt = jwtTokenProvider.encodeJwtToken(socialAuthType, oAuthResponse);
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

    private void updateRefreshToken(UserResponse userResponse, OAuthResponseVO oAuthResponseVO) {

        Optional<UserToken> optUserToken =
                userTokenRepository.findByRefreshToken(userResponse.getRefreshToken());

        optUserToken.ifPresentOrElse(
                findUserToken -> {
                    if(!findUserToken.getRefreshToken().equals(oAuthResponseVO.getRefreshToken()))
                        findUserToken.updateRefreshToken(oAuthResponseVO.getRefreshToken());
                }, UnAuthorizedException::new
        );
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
