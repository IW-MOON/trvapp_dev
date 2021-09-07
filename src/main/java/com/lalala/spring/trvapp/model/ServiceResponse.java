package com.lalala.spring.trvapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lalala.spring.trvapp.type.SocialAuthType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy)
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponse {

    private String state;
    private String code;
    private String idToken;
    private String user;
    private String accessToken;
    private String refreshToken;
    private String token;
    private String clientSecret;
    private boolean isJoined;
    //private SocialAuthType socialAuthType;
    private Long idx;

}
