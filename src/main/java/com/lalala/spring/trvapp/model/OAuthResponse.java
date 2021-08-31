package com.lalala.spring.trvapp.model;

import lombok.Data;

@Data
public class OAuthResponse {

    private String accessToken;
    private String expiresIn;
    private String refreshToken;
    private String scope;
    private String tokenType;
    private String idToken;
    private String code;
    private String state;
    private Object user;

}
