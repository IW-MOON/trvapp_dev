package com.lalala.spring.trvapp.vo.oauth;

import lombok.Getter;

@Getter
public class ApplePublicKey {

    private String kty;
    private String kid;
    private String use;
    private String alg;
    private String n;
    private String e;
}
