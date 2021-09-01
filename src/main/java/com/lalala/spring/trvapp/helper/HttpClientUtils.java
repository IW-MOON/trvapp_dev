package com.lalala.spring.trvapp.helper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.lalala.spring.trvapp.exception.ServerRuntimeException;
import com.lalala.spring.trvapp.exception.UnAuthorizedException;
import com.lalala.spring.trvapp.model.OAuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
@Component
public class HttpClientUtils {

    public Optional<ResponseEntity<String>> doPostResponseEntity(Map<String, Object> params, String url) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;

        try {

            responseEntity =
                    restTemplate.postForEntity(url, params, String.class);

            System.out.println("responseEntity.getStatusCode() = " + responseEntity.getStatusCode());

        } catch (Exception e) {
            throw new UnAuthorizedException();
        }
        return Optional.ofNullable(responseEntity);
    }

    public Optional<ResponseEntity<String>> doGetResponseEntity(Map<String, Object> params, String url) {

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity = null;
        try {

            responseEntity =
                    restTemplate.getForEntity(url, String.class, params );

        } catch (Exception e) {
            e.printStackTrace();
            throw new UnAuthorizedException();
        }
        return Optional.ofNullable(responseEntity);
    }

    public Optional<OAuthResponse> getPostOAuthResponse(Map<String, Object> params, String url) {

        Optional<ResponseEntity<String>> optionalResponseEntity = this.doPostResponseEntity(params, url);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return optionalResponseEntity.map(
                responseEntity -> {
                    try {
                        if (responseEntity.getStatusCode() == HttpStatus.OK) {
                            OAuthResponse oAuthResponse = mapper.readValue(responseEntity.getBody(), new TypeReference<OAuthResponse>() {
                            });
                            System.out.println("result = " + oAuthResponse);
                            return Optional.ofNullable(oAuthResponse);
                        }
                    } catch (Exception e){
                        throw new ServerRuntimeException();
                    }
                    throw new UnAuthorizedException();
                }

        ).orElseThrow(UnAuthorizedException::new);
    }
}
