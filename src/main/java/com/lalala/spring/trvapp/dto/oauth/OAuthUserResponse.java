package com.lalala.spring.trvapp.dto.oauth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Map;

@NoArgsConstructor
@Getter
public class OAuthUserResponse {

    private String id;
    private String email;
    private String name;

    @Builder
    public OAuthUserResponse(String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public static OAuthUserResponse from(DefaultOAuth2User defaultOAuth2User) {
        Map<String, Object> attributes = defaultOAuth2User.getAttributes();
        return OAuthUserResponse.builder()
                .id((String)attributes.get("id"))
                .email((String)attributes.get("email"))
                .name((String)attributes.get("name"))
                .build();
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
