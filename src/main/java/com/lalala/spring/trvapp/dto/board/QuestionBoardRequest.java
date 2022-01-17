package com.lalala.spring.trvapp.dto.board;


import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
public class QuestionBoardRequest {

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


}
