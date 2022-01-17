package com.lalala.spring.trvapp.config.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class TokenConfig {

    public static String SECRET_KEY;
    public static String AUTHORITIES_KEY;
    public static Long TOKEN_PERIOD;
    public static Long REFRESH_PERIOD;

    private TokenConfig() {

    }
    @Value("${token.secret_key}" )
    public void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
        SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
    }
    @Value("${token.authorities_key}" )
    public void setAuthoritiesKey(String authoritiesKey) {
        AUTHORITIES_KEY = authoritiesKey;
    }

    @Value("${token.token_period}" )
    public void setTokenPeriod(String tokenPeriod){
        TOKEN_PERIOD = Long.valueOf(tokenPeriod);
    }

    @Value("${token.refresh_period}" )
    public void setRefreshPeriod(String refreshPeriod){
        REFRESH_PERIOD = Long.valueOf(refreshPeriod);
    }
}
