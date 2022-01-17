package com.lalala.spring.trvapp.entity.board;

import com.lalala.spring.trvapp.dto.board.QuestionBoardRequest;
import com.lalala.spring.trvapp.entity.BaseEntity;
import com.lalala.spring.trvapp.entity.city.City;
import com.lalala.spring.trvapp.entity.user.User;
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
public class QuestionBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_BOARD_IDX",  nullable = false)
    private Long questionBoardIdx;

    @ManyToOne
    @JoinColumn(name = "USER_IDX")
    private User user;

    @ManyToOne
    @JoinColumn(name = "CITY_IDX")
    private City city;

    @Column(name = "MAIN_CATEGORY_IDX", nullable = false)
    private Long mainCategoryIdx;

    @Column(name = "SUB_CATEGORY_IDX")
    private Long subCategoryIdx;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENTS")
    @Lob
    private String contents;

    @Column(name = "LANGUAGE_IDX", nullable = false)
    private Long languageIdx;

    @Column(name = "DELETED")
    private Boolean isDeleted;

    public static QuestionBoard of(QuestionBoardRequest questionBoardRequest, User user, City city) {
        return QuestionBoard.builder()
                .user(user)
                .city(city)
                .mainCategoryIdx(questionBoardRequest.getMainCategoryIdx())
                .subCategoryIdx(questionBoardRequest.getSubCategoryIdx())
                .title(questionBoardRequest.getTitle())
                .contents(questionBoardRequest.getContents())
                .languageIdx(questionBoardRequest.getLanguageIdx())
                .isDeleted(false).build();
    }
}
