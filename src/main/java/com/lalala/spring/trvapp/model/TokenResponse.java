package com.lalala.spring.trvapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TokenResponse {

    private String token;
    private String accessToken;
    private String refreshToken;
}
