package com.lalala.spring.trvapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "policy")
public class OauthPolicyController {

    @GetMapping(value = "/facebook")
    public ModelAndView policy(){

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/policy/facebook.html");
        System.out.println("facebook");

        return mav;
    }
}
