package com.lalala.spring.trvapp.service.user;

import com.lalala.spring.trvapp.exception.UnAuthorizedException;
import com.lalala.spring.trvapp.helper.JwtTokenProvider;
import com.lalala.spring.trvapp.model.OAuthResponse;
import com.lalala.spring.trvapp.model.ServiceResponse;
import com.lalala.spring.trvapp.model.User;
import com.lalala.spring.trvapp.model.UserToken;
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

        Optional<OAuthResponse> result = oauthService.requestAccessToken(socialAuthType, serviceResponse);
        return result.map(r -> {

            User user = oauthService.getUserInfo(socialAuthType, r.getIdToken());
            log.debug(user.toString());

            Optional<User> optFindUser =
                    userRepository.findBySocialUniqId(user.getSocialUniqId());

            if(optFindUser.isPresent()){

                User findUser = optFindUser.get();
                findUser.updateLastLoginDtm();

                Optional<UserToken> optUserToken =
                userTokenRepository.findByUser(findUser);

                optUserToken.ifPresentOrElse(
                        findUserToken -> {
                            if(!findUserToken.getRefreshToken().equals(r.getRefreshToken()))
                                findUserToken.updateRefreshToken(r.getRefreshToken());
                        },
                        () -> saveRefreshToken(findUser, r.getRefreshToken())
                );

            } else {
                User saveUser = userRepository.save(user);
                saveRefreshToken(saveUser, r.getRefreshToken());
            }

            System.out.println("r = " + r.toString());

            String jwt = jwtTokenProvider.encodeJwtToken(r.getAccessToken(), r.getRefreshToken());
            return new ResponseEntity<ServiceResponse>(
                    ServiceResponse.builder()
                            .token(jwt)
                            .accessToken(r.getAccessToken())
                            .refreshToken(r.getRefreshToken())
                            .build(), HttpStatus.OK);

        }).orElseThrow(UnAuthorizedException::new);

    }

    public ResponseEntity<ServiceResponse> refreshToken(SocialAuthType socialAuthType, ServiceResponse serviceResponse){

        Optional<OAuthResponse> result = oauthService.refreshAccessToken(socialAuthType, serviceResponse);
        return result.map(r -> {

            String jwt = jwtTokenProvider.encodeJwtToken(r.getAccessToken(), r.getRefreshToken());
            return new ResponseEntity<ServiceResponse>(
                    ServiceResponse.builder()
                            .token(jwt)
                            .accessToken(r.getAccessToken())
                            .refreshToken(r.getRefreshToken())
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
