package com.lalala.spring.trvapp.helper;

import com.lalala.spring.trvapp.exception.ServerRuntimeException;
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
        Map<String, String> map = new LinkedHashMap<>();

        String wordsNounUrl = wordsApiUrl.concat(wordsNounUri);
        String wordsAdjectiveUrl = wordsApiUrl.concat(wordsAdjectiveUri);

        Optional<ResponseEntity<String[]>> optNoun =  httpClientUtils.doGetResponseEntityArray(map, wordsNounUrl);
        Optional<ResponseEntity<String[]>> optAdjective =  httpClientUtils.doGetResponseEntityArray(map, wordsAdjectiveUrl);

        int randNumber = (int)(Math.random()*10000);
        return optAdjective.map(
                adjective -> {
                    return optNoun.map(
                            noun -> {
                                nickName.append(Objects.requireNonNull(adjective.getBody())[0]);
                                nickName.append(Objects.requireNonNull(noun.getBody())[0]);
                                nickName.append(randNumber);
                                return nickName.toString();
                            }
                    ).orElseThrow(ServerRuntimeException::new);
                }
        ).orElseThrow(ServerRuntimeException::new);
    }

}
