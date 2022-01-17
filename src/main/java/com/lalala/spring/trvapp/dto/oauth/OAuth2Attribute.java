package com.lalala.spring.trvapp.dto.oauth;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@ToString
@AllArgsConstructor
@Builder
@Getter
public class OAuth2Attribute  {

    private Map<String, Object> attributes;
    private String attributeKey;
    private String id;
    private String name;
    private String email;


    public static OAuth2Attribute of(String provider, String attributeKey,
                              Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return ofGoogle("sub", attributes);
            case "facebook":
                return ofFaceBook("id", attributes);
            default:
                throw new RuntimeException();
        }
    }

    private static OAuth2Attribute ofGoogle(String attributeKey,
                                            Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                .id((String) attributes.get("sub"))
                .email((String) attributes.get("email"))
                .name((String) attributes.get("name"))
                .attributes(attributes)
                .attributeKey(attributeKey)
                .build();
    }

    private static OAuth2Attribute ofFaceBook(String attributeKey,
                                            Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                .id((String) attributes.get("email"))
                .email((String) attributes.get("email"))
                .name((String) attributes.get("name"))
                .attributes(attributes)
                .attributeKey(attributeKey)
                .build();
    }

    public Map<String, Object> convertToMap(String provider) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("key", attributeKey);
        map.put("email", email);
        map.put("name", name);
        map.put("provider", provider);
        return map;
    }

}
