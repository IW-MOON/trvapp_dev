package com.lalala.spring.trvapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.lalala.spring.trvapp.model.GoogleOAuthRequest;
import com.lalala.spring.trvapp.model.GoogleOAuthResponse;
import com.lalala.spring.trvapp.service.user.OauthService;
import com.lalala.spring.trvapp.type.SocialLoginType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;

@Slf4j
@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping(value = "user")
@RequiredArgsConstructor
public class UserController {

    private final OauthService oauthService;

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping(value = "/auth/{socialLoginType}")
    public ResponseEntity<GoogleOAuthResponse> callback(
            @PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
            @RequestParam(name = "code") String code) {

        System.out.println("code = " + code);
        log.info("callback code : {}", code);

        return oauthService.requestAccessToken(socialLoginType, code);
    }


}
