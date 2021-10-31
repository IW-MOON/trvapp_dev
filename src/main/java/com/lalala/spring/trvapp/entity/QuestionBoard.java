package com.lalala.spring.trvapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "QUESTION_BOARD")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionBoard extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_BOARD_IDX",  nullable = false)
    private Long questionBoardIdx;

    @Column(name = "USER_IDX", nullable = false)
    private Long userIdx;

    @Column(name = "CITY_IDX", nullable = false)
    private Long cityIdx;

    @Column(name = "MAIN_CATEGORY_IDX", nullable = false)
    private Long mainCategoryIdx;

    @Column(name = "SUB_CATEGORY_IDX")
    private Long subCategoryIdx;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENTS")
    private String contents;

    @Column(name = "LANGUAGE_IDX", nullable = false)
    private Long languageIdx;

    @Column(name = "SUGGESTION1")
    private String suggestion1;

    @Column(name = "SUGGESTION2")
    private String suggestion2;

    @Column(name = "SUGGESTION3")
    private String suggestion3;

    @Column(name = "SUGGESTION4")
    private String suggestion4;

    @Column(name = "SUGGESTION5")
    private String suggestion5;

}
