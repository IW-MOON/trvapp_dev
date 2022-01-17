package com.lalala.spring.trvapp.entity.token;

import com.lalala.spring.trvapp.config.token.TokenConfig;
import io.jsonwebtoken.*;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;


@Slf4j
@ToString
public class Token {

    private String token;

    public Token(String token) {
        this.token = token;
    }

    public boolean verifyToken() {
        try {
            Jwts.parserBuilder().setSigningKey(TokenConfig.SECRET_KEY).build().parseClaimsJws(token);
            return true;
//            Jws<Claims> claims = Jwts.parser()
//                    .setSigningKey(TokenConfig.SECRET_KEY)
//                    .parseClaimsJws(token);
//            return claims.getBody()
//                    .getExpiration()
//                    .after(new Date());
        }  catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        } catch (Exception ignored) {
            log.info(ignored.getMessage());
        }
        return false;
    }

    public String getUid() {
        try {
            return Jwts.parser().setSigningKey(TokenConfig.SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e){
            throw new IllegalArgumentException();
        }
    }

    public String getId() {
        try {
            return Jwts.parser().setSigningKey(TokenConfig.SECRET_KEY).parseClaimsJws(token).getBody().getId();
        } catch (Exception e){
            throw new IllegalArgumentException();
        }
    }

    public String getToken() {
        return token;
    }

    public Authentication getAuthentication() {
        // 토큰 복호화
        Claims claims = parseClaims(token);

        if (claims.get(TokenConfig.AUTHORITIES_KEY) == null) {
            throw new IllegalArgumentException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(TokenConfig.AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(TokenConfig.SECRET_KEY).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
