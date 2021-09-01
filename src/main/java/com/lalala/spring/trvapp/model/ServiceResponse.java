package com.lalala.spring.trvapp.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy)
public class ServiceResponse {

    private String state;
    private String code;
    private String idToken;
    private String user;
    private String accessToken;
    private String refreshToken;
    private String token;
    private String clientSecret;


}
