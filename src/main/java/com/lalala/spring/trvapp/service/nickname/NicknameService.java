package com.lalala.spring.trvapp.service.nickname;

import com.lalala.spring.trvapp.repository.NicknameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class NicknameService {

    private final NicknameRepository nicknameRepository;

    public String generateNickName(){

        StringBuilder nickName = new StringBuilder();

        String noun = nicknameRepository.findByType("noun").getWord();
        String adjective = nicknameRepository.findByType("adjective").getWord();
        int randNumber = (int)(Math.random()*10000);

        nickName.append(noun).append("-").append(adjective).append(randNumber);
        return nickName.toString();
    }

}
