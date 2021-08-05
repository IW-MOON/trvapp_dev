package com.lalala.spring.trvapp.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
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

//@Slf4j
//@RestController
//@CrossOrigin(maxAge = 3600)
//@RequestMapping(value = "user")
@RequiredArgsConstructor
public class UserController_back {

    @Value("${external.auth.google.client_secret}")
    private String clientSecret;

    @Value("${external.auth.google.client_id}")
    private String clientId;
//    @Value("${external.auth.google.client_secret}")
//    private String clientSecret;


//    @CrossOrigin(origins = "http://localhost:8080")
//    @PostMapping(value = "/auth/{socialLoginType}")
//    public ResponseEntity<Void> main(@RequestBody HashMap<String, String> map) {
//        System.out.println("map = " + map);
//        String clientIdToken = map.get("idToken").toString();
//
//        System.out.println("clientIdToken = " + clientIdToken);
//
//        HttpTransport transport = new NetHttpTransport();
//        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
//
//        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
//                .setAudience(Collections.singletonList(clientId))
//                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
//                .build();
//        String userId = null;
//        String email = null;
//
//
//        try {
//            GoogleIdToken idToken = verifier.verify(clientIdToken);
//            GoogleIdToken.Payload payload = idToken.getPayload();
//
//            System.out.println("payload = " + payload);
//            userId = payload.getSubject();
//            email = payload.getEmail();
//
//            log.debug(userId);
//            log.debug(email);
//            System.out.println("userId = " + userId);
//
//
//        } catch (GeneralSecurityException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
}
