package com.lalala.spring.trvapp.helper;

import com.lalala.spring.trvapp.type.SocialAuthType;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class SocialAuthTypeConverter implements Converter<String, SocialAuthType> {
    @Override
    public SocialAuthType convert(String source) {
        return SocialAuthType.valueOf(source.toUpperCase());
    }
}
