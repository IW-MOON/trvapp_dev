package com.lalala.spring.trvapp.vo.oauth;

import lombok.Data;

@Data
public class OAuthResponseVO {

    private String accessToken;
    private long expiresIn;
    private String refreshToken;
    private String scope;
    private String tokenType;
    private String idToken;
    private String code;
    private String state;
    private Object user;
    private String clientSecret;
    private String name;
    private String id;
    private String email;
}
