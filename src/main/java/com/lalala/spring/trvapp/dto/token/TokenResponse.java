package com.lalala.spring.trvapp.dto.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TokenResponse {

    private String token;
    private String refreshToken;

    private LocalDateTime creationDtm;
    private LocalDateTime expirationDtm;

}
