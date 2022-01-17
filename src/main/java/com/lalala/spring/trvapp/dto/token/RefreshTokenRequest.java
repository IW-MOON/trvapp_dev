package com.lalala.spring.trvapp.dto.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenRequest {

    private final String refreshToken;
    private final String clientSecret;


}
