package com.trv.trvapp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
////@PropertySource( value = "file:./application.yml", ignoreResourceNotFound = true)
public class HelloController {

//    @Value("${logging-module.version}")
//    private String version;
//
//    @GetMapping("/")
//    public String version() {
//        return String.format("Project Version : %s", version);
//
//    }
//
    @GetMapping("/health")
    public String checkHealth() {
        return "healthy";
    }

}
