package com.lalala.spring.trvapp.controller.user;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.lalala.spring.trvapp.dto.token.TokenResponse;
import com.lalala.spring.trvapp.dto.oauth.OAuthResponse;
import com.lalala.spring.trvapp.config.oauth.AuthConfig;
import com.lalala.spring.trvapp.entity.token.Token;
import com.lalala.spring.trvapp.entity.user.User;
import com.lalala.spring.trvapp.interceptor.AuthenticationPrincipal;
import com.lalala.spring.trvapp.service.user.UserService;
import com.lalala.spring.trvapp.type.SocialAuthType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Slf4j
@RestController
//@CrossOrigin(maxAge = 3600)
@RequestMapping(value = "user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @RequestMapping( value = "/auth/{socialLoginType}", method = {RequestMethod.POST})
    public ResponseEntity callback(
            @PathVariable(name = "socialLoginType") SocialAuthType socialAuthType,
            @RequestParam Map<String, Object> responseMap
    )
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        if(socialAuthType == SocialAuthType.APPLE){
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        }

        OAuthResponse OAuthResponse = mapper.convertValue(responseMap, OAuthResponse.class);
        TokenResponse tokenResponse =  userService.auth(socialAuthType, OAuthResponse);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AuthConfig.HEADER_NAME_TOKEN_AUTH, tokenResponse.getToken());
        httpHeaders.add(AuthConfig.HEADER_NAME_TOKEN_REFRESH, tokenResponse.getRefreshToken());

        return new ResponseEntity(httpHeaders, HttpStatus.OK);
    }

    @PostMapping( value = "/join/{socialLoginType}")
    public ResponseEntity<Void> join(
            @PathVariable(name = "socialLoginType") SocialAuthType socialAuthType,
            @AuthenticationPrincipal @Nullable Object object
    ) {
        log.info(socialAuthType.toString());

        if(object instanceof User){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        if (object instanceof String) {
            String token = (String) object;
            TokenResponse tokenResponse = userService.join(socialAuthType, new Token(token));

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(AuthConfig.HEADER_NAME_TOKEN_AUTH, tokenResponse.getToken());
            httpHeaders.add(AuthConfig.HEADER_NAME_TOKEN_REFRESH, tokenResponse.getRefreshToken());
            return new ResponseEntity<>(httpHeaders, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @GetMapping(value = "/check")
    public ResponseEntity check(
            HttpServletRequest request

    ) {
        System.out.println("access_token = " + request.getAttribute("access_token"));
        System.out.println("refresh_token = " + request.getAttribute("refresh_token"));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/endpoint")
    public ResponseEntity<OAuthResponse> endpoint(String payload)
    {
        log.info(payload);
        userService.processEndpoint(payload);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity handleDataAccessException(DataAccessException e) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgumentException(DataAccessException e) {
        return ResponseEntity.badRequest().build();
    }
}

