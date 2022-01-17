package com.lalala.spring.trvapp.config;

import com.lalala.spring.trvapp.interceptor.BearerAuthInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class AppConfig implements WebMvcConfigurer {

    private final BearerAuthInterceptor bearerAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        System.out.println(">>> 인터셉터 등록");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/user/check");
    }
}
