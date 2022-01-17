package com.lalala.spring.trvapp.dto.board;


import com.lalala.spring.trvapp.dto.city.CityResponse;
import com.lalala.spring.trvapp.dto.user.UserResponse;
import com.lalala.spring.trvapp.entity.city.City;
import com.lalala.spring.trvapp.entity.user.User;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class QuestionBoardResponse {

    private Long questionBoardIdx;
    private User user;
    private City city;
    private Long mainCategoryIdx;
    private Long subCategoryIdx;
    private String title;
    private String contents;
    private Long languageIdx;
    private boolean isDeleted;
    private LocalDateTime sysCretDtm ;
    private LocalDateTime sysChngDtm ;

    private UserResponse userResponse;
    private CityResponse cityResponse;

    public Long getQuestionBoardIdx() {
        return questionBoardIdx;
    }

    public UserResponse getUserResponse() {
        return new UserResponse(user.getUserIdx(), user.getNickName());
    }

    public CityResponse getCityResponse() {
        return new CityResponse(city.getCityName(), city.getCountryName());
    }

    public Long getMainCategoryIdx() {
        return mainCategoryIdx;
    }

    public Long getSubCategoryIdx() {
        return subCategoryIdx;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public Long getLanguageIdx() {
        return languageIdx;
    }

    public LocalDateTime getSysCretDtm() {
        return sysCretDtm;
    }

    public LocalDateTime getSysChngDtm() {
        return sysChngDtm;
    }
}
