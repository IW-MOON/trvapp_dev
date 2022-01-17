package com.lalala.spring.trvapp.service.token;

import com.lalala.spring.trvapp.config.token.TokenConfig;
import com.lalala.spring.trvapp.dto.token.TokenResponse;
import com.lalala.spring.trvapp.entity.role.RoleType;
import com.lalala.spring.trvapp.entity.token.Token;
import com.lalala.spring.trvapp.entity.user.User;
import com.lalala.spring.trvapp.entity.token.UserToken;
import com.lalala.spring.trvapp.repository.token.UserTokenRepository;
import com.lalala.spring.trvapp.repository.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenService {

    private final UserTokenRepository userTokenRepository;
    private final UserRepository userRepository;


    public TokenResponse generateToken(String id, String uid, List<RoleType> roleTypes) {

        Claims claims = Jwts.claims()
                        .setSubject(uid).setId(id);

        String authorities = roleTypes.stream()
                                    .map(Enum::name)
                                    .collect(Collectors.joining(","));

        claims.put(TokenConfig.AUTHORITIES_KEY, authorities);

        Date now = new Date();
        return new TokenResponse(
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + TokenConfig.TOKEN_PERIOD))
                        .signWith(SignatureAlgorithm.HS256, TokenConfig.SECRET_KEY)
                        .compact(),
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + TokenConfig.REFRESH_PERIOD))
                        .signWith(SignatureAlgorithm.HS256, TokenConfig.SECRET_KEY)
                        .compact(),
                new Timestamp(now.getTime()).toLocalDateTime(),
                new Timestamp(now.getTime() + TokenConfig.REFRESH_PERIOD).toLocalDateTime());

    }

    public TokenResponse refreshToken(String refreshToken){

        if(refreshToken == null){
            throw new IllegalArgumentException("RefreshToken is Null");
        }

        Token token = new Token(refreshToken);
        if (!token.verifyToken()) {
            throw new IllegalArgumentException("Invalid Token");
        }
        String uid = token.getUid();
        String id = token.getId();

        User user = userRepository.findBySocialUniqId(id).orElseThrow(() -> new IllegalArgumentException("Not Found User!"));
        TokenResponse tokenResponse = generateToken(id, uid, user.getRoleTypes());
        this.saveRefreshToken(user, tokenResponse);
        return tokenResponse;
    }

    public void saveRefreshToken(User user, TokenResponse tokenResponse){

        Optional<UserToken> userTokenOptional = userTokenRepository.findByUser(user);
        if(userTokenOptional.isEmpty()){
            UserToken userToken = UserToken.builder()
                    .user(user)
                    .refreshToken(tokenResponse.getRefreshToken())
                    .creationDtm(tokenResponse.getCreationDtm())
                    .expirationDtm(tokenResponse.getExpirationDtm()).build();
            userTokenRepository.save(userToken);
            return;
        }

        UserToken userToken = userTokenOptional.get();
        userToken.updateRefreshToken(tokenResponse);
    }

}
