package com.lalala.spring.trvapp.controller;



import com.lalala.spring.trvapp.model.ServiceResponse;
import com.lalala.spring.trvapp.service.user.UserService;
import com.lalala.spring.trvapp.type.SocialAuthType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping(value = "user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 로그인
    //CrossOrigin(origins = "http://localhost:8080")
    @GetMapping(value = "/auth/{socialLoginType}")
    public ResponseEntity<ServiceResponse> callback(
            @PathVariable(name = "socialLoginType") SocialAuthType socialAuthType,
            ServiceResponse serviceResponse
            ) {
        return userService.auth(socialAuthType, serviceResponse);
    }

    @GetMapping(value = "/auth/{socialLoginType}/refresh_token")
    public ResponseEntity<ServiceResponse> refreshToken(
            @PathVariable(name = "socialLoginType") SocialAuthType socialAuthType,
            ServiceResponse serviceResponse
    ) {

        return userService.refreshToken(socialAuthType, serviceResponse);
    }
    @GetMapping(value = "/check")
    public ResponseEntity check(
            //@RequestParam(name = "access_token") String access_token, @RequestParam(name = "refresh_token") String refresh_token
            HttpServletRequest request

    ) {
            //return responseEntity.getBody();
        System.out.println("access_token = " + request.getAttribute("access_token"));
        System.out.println("refresh_token = " + request.getAttribute("refresh_token"));

         return new ResponseEntity<>(HttpStatus.OK);

    }
}

