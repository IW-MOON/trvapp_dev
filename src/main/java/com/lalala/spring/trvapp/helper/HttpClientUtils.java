package com.lalala.spring.trvapp.helper;


import com.lalala.spring.trvapp.exception.ServerRuntimeException;
import com.lalala.spring.trvapp.exception.UnAuthorizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
@Component
public class HttpClientUtils {

    public Optional<ResponseEntity<String>> doPostResponseEntity(MultiValueMap<String, String> params, String url) {

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

    public Optional<ResponseEntity<String>> doGetResponseEntity(MultiValueMap<String, String> params, String url) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(url).queryParams(params).build().toUri();
            responseEntity =
                    restTemplate.getForEntity(uri, String.class );

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerRuntimeException();
        }
        return Optional.of(responseEntity);
    }

    public Optional<ResponseEntity<String[]>> doGetResponseEntityArray(Map<String, String> params, String url) {

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
