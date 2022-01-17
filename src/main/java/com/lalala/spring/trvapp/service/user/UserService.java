package com.lalala.spring.trvapp.service.user;

import com.lalala.spring.trvapp.dto.token.TokenResponse;
import com.lalala.spring.trvapp.entity.role.Role;
import com.lalala.spring.trvapp.entity.role.RoleType;
import com.lalala.spring.trvapp.entity.role.UserRole;
import com.lalala.spring.trvapp.entity.token.Token;
import com.lalala.spring.trvapp.exception.UnAuthorizedException;
import com.lalala.spring.trvapp.dto.oauth.OAuthResponse;
import com.lalala.spring.trvapp.entity.user.User;
import com.lalala.spring.trvapp.repository.role.UserRoleRepository;
import com.lalala.spring.trvapp.repository.user.UserRepository;
import com.lalala.spring.trvapp.repository.token.UserTokenRepository;
import com.lalala.spring.trvapp.service.nickname.NicknameService;
import com.lalala.spring.trvapp.service.oauth.OauthService;
import com.lalala.spring.trvapp.service.role.RoleService;
import com.lalala.spring.trvapp.service.token.TokenService;
import com.lalala.spring.trvapp.type.SocialAuthType;
import com.lalala.spring.trvapp.vo.oauth.OAuthResponseVO;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final OauthService oauthService;
    private final NicknameService nicknameService;
    private final TokenService tokenService;
    private final RoleService roleService;


    public TokenResponse auth(SocialAuthType socialAuthType, OAuthResponse OAuthResponse){

        if(OAuthResponse.getCode() == null){
            throw new UnAuthorizedException("Code Value Is Null!");
        }
        OAuthResponseVO oAuthResponseVO = oauthService.requestAccessToken(socialAuthType, OAuthResponse);

        log.debug("oAuthResponseVO : {} , ", oAuthResponseVO);
        String idToken = oAuthResponseVO.getIdToken();

        User user = oauthService.getUserInfo(socialAuthType, idToken, oAuthResponseVO.getAccessToken());
        return tokenService.generateToken(user.getSocialUniqId(), user.getEmail(), user.getRoleTypes());
    }

    public TokenResponse join(SocialAuthType socialAuthType, Token token) throws UnAuthorizedException {


        if( token == null || !token.verifyToken()){
            throw new IllegalArgumentException("Invalid Token Value");
        }

        String id = token.getId();
        String email = token.getUid();

        User saveUser = saveUser(socialAuthType, id, email);
        Role role = roleService.getRoleByRoleType(RoleType.ROLE_USER);
        UserRole userRole = userRoleRepository.save(new UserRole(saveUser, role));
        saveUser.addRole(userRole);
        TokenResponse tokenResponse = tokenService.generateToken(id, email, saveUser.getRoleTypes());
        tokenService.saveRefreshToken(saveUser, tokenResponse);

        return tokenResponse;
    }

    private User saveUser(SocialAuthType socialAuthType, String id, String email) {

        String nickName = nicknameService.generateNickName();
        User user = User.builder()
                .socialUniqId(id)
                .email(email)
                .nickName(nickName)
                .socialAuthType(socialAuthType)
                .build();

        log.info(user.toString());

        return userRepository.save(user);
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

    public User getUserBySocialUniqId(String socialUniqId){
        return userRepository.findBySocialUniqId(socialUniqId).orElseThrow(() -> new IllegalArgumentException("Not Found User!"));
    }


}
