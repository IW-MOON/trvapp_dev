package com.lalala.spring.trvapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping()
@RequiredArgsConstructor
public class MainController {


    @GetMapping(value = "/")
    public ResponseEntity<Void> main() {

        System.out.println("ymoymo");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/health")
    public String checkHealth() {
        return "healthy";
    }
}
