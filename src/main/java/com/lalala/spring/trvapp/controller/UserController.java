package com.lalala.spring.trvapp.controller;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.lalala.spring.trvapp.model.ServiceResponse;
import com.lalala.spring.trvapp.service.user.UserService;
import com.lalala.spring.trvapp.type.SocialAuthType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Slf4j
@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping(value = "user")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    /*
    * (Apple) Login 유저 정보를 받은 후 권한 생성
    *
    * @param serviceResponse
    * @return
    * */
    // 로그인
    //CrossOrigin(origins = "http://localhost:8080")
    @RequestMapping( value = "/auth/{socialLoginType}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ServiceResponse> callback(
            @PathVariable(name = "socialLoginType") SocialAuthType socialAuthType,
            @RequestParam Map<String, Object> responseMap
            ) {
        log.info(responseMap.toString());
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        if(socialAuthType == SocialAuthType.APPLE){
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        }

        ServiceResponse serviceResponse = mapper.convertValue(responseMap, ServiceResponse.class);
        log.info(socialAuthType.toString());
        log.info(serviceResponse.toString());
        //return  new ResponseEntity<>(HttpStatus.OK);
        return userService.auth(socialAuthType, serviceResponse);
    }

    @PostMapping( value = "/join/{socialLoginType}")
    public ResponseEntity<ServiceResponse> join(
            @PathVariable(name = "socialLoginType") SocialAuthType socialAuthType,
            ServiceResponse serviceResponse
    ) {
        log.info(socialAuthType.toString());
        log.info(serviceResponse.toString());

        return userService.join(socialAuthType, serviceResponse);
    }

    @PostMapping(value = "/auth/{socialLoginType}/refresh_token")
    public ResponseEntity<ServiceResponse> refreshToken(
            @PathVariable(name = "socialLoginType") SocialAuthType socialAuthType,
            ServiceResponse serviceResponse
    ) {

        return userService.refreshToken(socialAuthType, serviceResponse);
    }

    @GetMapping(value = "/check")
    public ResponseEntity check(
            HttpServletRequest request

    ) {
        System.out.println("access_token = " + request.getAttribute("access_token"));
        System.out.println("refresh_token = " + request.getAttribute("refresh_token"));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/endpoint")
    public ResponseEntity<ServiceResponse> endpoint(String payload)
    {
        log.info(payload);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

