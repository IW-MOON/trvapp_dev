package com.lalala.spring.trvapp.controller.token;

import com.lalala.spring.trvapp.dto.token.RefreshTokenRequest;
import com.lalala.spring.trvapp.config.oauth.AuthConfig;
import com.lalala.spring.trvapp.dto.token.TokenResponse;
import com.lalala.spring.trvapp.interceptor.AuthenticationPrincipal;
import com.lalala.spring.trvapp.service.token.TokenService;
import com.lalala.spring.trvapp.type.SocialAuthType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TokenController {

    private final TokenService tokenService;

    @GetMapping("/token/expired")
    public String auth(HttpServletRequest request, HttpServletResponse response) {
        log.info("request : {}", request);
        throw new RuntimeException();
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<Void> refreshToken(@AuthenticationPrincipal String refreshToken)  {

//        if (socialAuthType == SocialAuthType.FACEBOOK) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
        //RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(token, clientSecret);
        TokenResponse tokenResponse = tokenService.refreshToken(refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AuthConfig.HEADER_NAME_TOKEN_AUTH, tokenResponse.getToken());
        httpHeaders.add(AuthConfig.HEADER_NAME_TOKEN_REFRESH, tokenResponse.getRefreshToken());

        return new ResponseEntity<>(httpHeaders, HttpStatus.OK);

    }
}
