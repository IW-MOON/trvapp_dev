package com.lalala.spring.trvapp.helper;


import com.lalala.spring.trvapp.exception.ServerRuntimeException;
import com.lalala.spring.trvapp.exception.UnAuthorizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
@Component
public class HttpClientUtils {

    public Optional<ResponseEntity<String>> doPostResponseEntity(MultiValueMap<String, Object> params, String url) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;

        try {

            responseEntity =
                    restTemplate.postForEntity(url, params, String.class);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerRuntimeException();
        }
        return Optional.of(responseEntity);
    }

    public Optional<ResponseEntity<String>> doGetResponseEntity(MultiValueMap<String, Object> params, String url) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity =
                    restTemplate.getForEntity(url, String.class, params );

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerRuntimeException();
        }
        return Optional.of(responseEntity);
    }

    public Optional<ResponseEntity<String[]>> doGetResponseEntityArray(Map<String, Object> params, String url) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String[]> responseEntity = null;
        try {

            responseEntity =
                    restTemplate.getForEntity(url, String[].class, params );

        } catch (Exception e) {
            e.printStackTrace();
            throw new UnAuthorizedException();
        }
        return Optional.of(responseEntity);
    }

}
