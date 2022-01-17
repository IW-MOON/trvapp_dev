package com.lalala.spring.trvapp.dto.board;


import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
public class TipBoardRequest {

    @NotNull
    private Long cityIdx;
    @NotNull
    private Long mainCategoryIdx;
    private Long subCategoryIdx;
    @NotNull
    private String title;
    @NotNull
    private String contents;
    @NotNull
    private Long languageIdx;
    private String suggestion1;
    private String suggestion2;
    private String suggestion3;
    private String suggestion4;
    private String suggestion5;

}
