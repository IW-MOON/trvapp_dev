package com.lalala.spring.trvapp.service.user;

import com.lalala.spring.trvapp.exception.ServerRuntimeException;
import com.lalala.spring.trvapp.helper.HttpClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WordsGenerate {

    private final HttpClientUtils httpClientUtils;

    private final String wordsApiUrl = "https://random-word-form.herokuapp.com";
    private final String wordsNounUri = "/random/noun";
    private final String wordsAdjectiveUri = "/random/adjective";

    public String generateNickName() throws ServerRuntimeException{

        StringBuffer nickName = new StringBuffer();
        Map<String, Object> map = new LinkedHashMap<>();

        String wordsNounUrl = wordsApiUrl.concat(wordsNounUri);
        String wordsAdjectiveUrl = wordsApiUrl.concat(wordsAdjectiveUri);

        Optional<ResponseEntity<String[]>> optNoun =  httpClientUtils.doGetResponseEntityArray(map, wordsNounUrl);
        Optional<ResponseEntity<String[]>> optAdjective =  httpClientUtils.doGetResponseEntityArray(map, wordsAdjectiveUrl);

        return optAdjective.map(
                adjective -> {
                    return optNoun.map(
                            noun -> {
                                nickName.append(adjective.getBody()[0]);
                                nickName.append(noun.getBody()[0]);
                                return nickName.toString();
                            }
                    ).orElseThrow(ServerRuntimeException::new);
                }
        ).orElseThrow(ServerRuntimeException::new);
    }

}
