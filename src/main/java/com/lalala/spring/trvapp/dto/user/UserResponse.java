package com.lalala.spring.trvapp.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long userIdx;
    private String nickName;

}
