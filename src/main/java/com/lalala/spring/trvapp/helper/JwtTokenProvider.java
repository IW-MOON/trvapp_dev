package com.lalala.spring.trvapp.helper;

import com.lalala.spring.trvapp.type.SocialAuthType;
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

    public String encodeJwtToken(SocialAuthType socialAuthType,  String accessToken, String refreshToken) {

        String jwt = null;
        try {
            Date now = new Date();
            long expTime;
            if (socialAuthType == SocialAuthType.APPLE){
                expTime = 3600000L * 24;	// 유효시간 : 1DAY
            } else {
                expTime = 3600000L;	// 유효시간 : 1H
            }

            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes("UTF-8"));

            Map<String, Object> header = new HashMap<>();
            header.put("typ", typ);
            header.put("alg", alg);

            jwt = Jwts.builder()
                    .setHeader(header)
                    .setIssuer("LaLaLa_TrvApp")
                    .setIssuedAt(now)
                    .setExpiration(new Date(System.currentTimeMillis() + expTime))
                    .claim("access_token", accessToken)
                    .claim("refresh_token", refreshToken)
                    .signWith(key)
                    .compact();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return jwt;
    }

    public boolean validateToken(String jwt) {
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
