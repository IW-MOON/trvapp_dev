package com.lalala.spring.trvapp.helper;

import com.lalala.spring.trvapp.type.SocialAuthType;
import com.lalala.spring.trvapp.vo.oauth.OAuthResponseVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${token.type}")
    private String typ;
    @Value("${token.alg}")
    private String alg;
    @Value("${token.secret-key}")
    private String secretKey;
    @Value("${token.expire_time.apple}")
    private long appleExpTime;
    @Value("${token.expire_time.google}")
    private long googleExpTime;
    @Value("${token.issuer}")
    private String issuer;


    public String encodeJwtToken(SocialAuthType socialAuthType, OAuthResponseVO oAuthResponseVO) {

        String jwt = null;
        try {
            Date now = new Date();
            long expTime = oAuthResponseVO.getExpiresIn();

            if (socialAuthType == SocialAuthType.APPLE){
                expTime = appleExpTime;	// 유효시간 : 1DAY
            } else if(socialAuthType == SocialAuthType.GOOGLE){
                expTime = googleExpTime;	// 유효시간 : 1H
            }

            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes("UTF-8"));

            Map<String, Object> header = new HashMap<>();
            header.put("typ", typ);
            header.put("alg", alg);

            jwt = Jwts.builder()
                    .setHeader(header)
                    .setIssuer(issuer)
                    .setIssuedAt(now)
                    .setExpiration(new Date(System.currentTimeMillis() + expTime))
                    .claim("access_token", oAuthResponseVO.getAccessToken())
                    .claim("refresh_token", oAuthResponseVO.getRefreshToken())
                    .signWith(key)
                    .compact();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return jwt;
    }

    public boolean isValidateToken(String jwt) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey.getBytes("UTF-8")).parseClaimsJws(jwt);
            Date exp = claims.getBody().getExpiration();
            Date now = new Date();

            if(exp.after(now))
                return true;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public String getClaim(String jwt, String claimName) {

        try {
            return Jwts.parser().setSigningKey(secretKey.getBytes("UTF-8")).parseClaimsJws(jwt).getBody().get(claimName, String.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Strings.EMPTY;
        }
    }


}
