package com.lalala.spring.trvapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ServiceResponse {

    private String state;
    private String code;
    private String idToken;
    private String user;
    private String accessToken;
    private String refreshToken;
    private String token;

}
