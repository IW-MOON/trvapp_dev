package com.lalala.spring.trvapp.entity.board;

import com.lalala.spring.trvapp.dto.board.TipBoardRequest;
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
@Table(name = "TIP_BOARD")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TIP_BOARD_IDX",  nullable = false)
    private Long tipBoardIdx;

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

    @Column(name = "DELETED")
    private Boolean isDeleted;

    public static TipBoard of(TipBoardRequest tipBoardRequest, User user, City city) {
        return TipBoard.builder()
                .user(user)
                .city(city)
                .mainCategoryIdx(tipBoardRequest.getMainCategoryIdx())
                .subCategoryIdx(tipBoardRequest.getSubCategoryIdx())
                .title(tipBoardRequest.getTitle())
                .contents(tipBoardRequest.getContents())
                .suggestion1(tipBoardRequest.getSuggestion1())
                .suggestion2(tipBoardRequest.getSuggestion2())
                .suggestion3(tipBoardRequest.getSuggestion3())
                .suggestion4(tipBoardRequest.getSuggestion4())
                .suggestion5(tipBoardRequest.getSuggestion5())
                .languageIdx(tipBoardRequest.getLanguageIdx())
                .isDeleted(false).build();
    }
    
}
