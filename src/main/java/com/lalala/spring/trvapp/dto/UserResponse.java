package com.lalala.spring.trvapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private String state;
    private String code;
    private String idToken;
    private String user;
    private String accessToken;
    private String refreshToken;
    private String token;
    private String clientSecret;
    private boolean isJoined;
    private Long idx;

}
