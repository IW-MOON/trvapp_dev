package com.lalala.spring.trvapp.service.user;

import com.lalala.spring.trvapp.exception.UnAuthorizedException;
import com.lalala.spring.trvapp.helper.JwtTokenProvider;
import com.lalala.spring.trvapp.model.OAuthResponse;
import com.lalala.spring.trvapp.model.ServiceResponse;
import com.lalala.spring.trvapp.entity.User;
import com.lalala.spring.trvapp.entity.UserToken;
import com.lalala.spring.trvapp.repository.UserRepository;
import com.lalala.spring.trvapp.repository.UserTokenRepository;
import com.lalala.spring.trvapp.type.SocialAuthType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final OauthService oauthService;
    private final JwtTokenProvider jwtTokenProvider;

    public ResponseEntity<ServiceResponse> auth(SocialAuthType socialAuthType, ServiceResponse serviceResponse){

        if(serviceResponse.getCode() == null){
            throw new UnAuthorizedException();
        }

        Optional<OAuthResponse> optOAuthResponse = oauthService.requestAccessToken(socialAuthType, serviceResponse);
        return optOAuthResponse.map(oAuthResponse -> {

            User user = oauthService.getUserInfo(socialAuthType, oAuthResponse);
            log.info(user.toString());

            Optional<User> optFindUser =
                    userRepository.findBySocialUniqId(user.getSocialUniqId());

            if(optFindUser.isPresent()){

                User findUser = optFindUser.get();
                findUser.updateLastLoginDtm();

                Optional<UserToken> optUserToken =
                userTokenRepository.findByUser(findUser);

                optUserToken.ifPresentOrElse(
                        findUserToken -> {
                            if(!findUserToken.getRefreshToken().equals(oAuthResponse.getRefreshToken()))
                                findUserToken.updateRefreshToken(oAuthResponse.getRefreshToken());
                        },
                        () -> saveRefreshToken(findUser, oAuthResponse.getRefreshToken())
                );

            } else {
                User saveUser = userRepository.save(user);
                saveRefreshToken(saveUser, oAuthResponse.getRefreshToken());
            }

            System.out.println("r = " + oAuthResponse.toString());

            String jwt = jwtTokenProvider.encodeJwtToken(socialAuthType, oAuthResponse.getAccessToken(), oAuthResponse.getRefreshToken());
            return new ResponseEntity<ServiceResponse>(
                    ServiceResponse.builder()
                            .token(jwt)
                            .accessToken(oAuthResponse.getAccessToken())
                            .refreshToken(oAuthResponse.getRefreshToken())
                            .clientSecret(oAuthResponse.getClientSecret())
                            .build(), HttpStatus.OK);

        }).orElseThrow(UnAuthorizedException::new);

    }

    public ResponseEntity<ServiceResponse> refreshToken(SocialAuthType socialAuthType, ServiceResponse serviceResponse){

        if(socialAuthType == SocialAuthType.APPLE){
            if(serviceResponse.getClientSecret() == null){
                throw new UnAuthorizedException();
            }
        }
        if(serviceResponse.getRefreshToken() == null){
            throw new UnAuthorizedException();
        }

        Optional<OAuthResponse> optOAuthResponse = oauthService.refreshAccessToken(socialAuthType, serviceResponse);
        return optOAuthResponse.map(oAuthResponse -> {

            String jwt = jwtTokenProvider.encodeJwtToken(socialAuthType, oAuthResponse.getAccessToken(), serviceResponse.getRefreshToken());
            return new ResponseEntity<ServiceResponse>(
                    ServiceResponse.builder()
                            .token(jwt)
                            .accessToken(oAuthResponse.getAccessToken())
                            .refreshToken(serviceResponse.getRefreshToken())
                            .build(), HttpStatus.OK);

        }).orElseThrow(UnAuthorizedException::new);

    }

    public void saveRefreshToken(User user, String refreshToken){

        if(refreshToken != null){
            UserToken userToken = UserToken.builder()
                    .user(user)
                    .refreshToken(refreshToken)
                    .creationDtm(LocalDateTime.now()).build();
            userTokenRepository.save(userToken);
        }
    }


}
