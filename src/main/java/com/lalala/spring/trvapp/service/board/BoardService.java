package com.lalala.spring.trvapp.service.board;

import com.lalala.spring.trvapp.entity.board.QuestionBoard;
import com.lalala.spring.trvapp.entity.board.TipBoard;
import com.lalala.spring.trvapp.repository.board.QuestionBoardRepository;
import com.lalala.spring.trvapp.repository.board.TipBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class BoardService {

    private final TipBoardRepository tipBoardRepository;
    private final QuestionBoardRepository questionBoardRepository;

    public List<TipBoard> getTipBoardList() {
        return tipBoardRepository.findByIsDeletedIsFalse();
    }

    public TipBoard insertTipBoard(TipBoard tipBoard) {
        return tipBoardRepository.save(tipBoard);
    }

    public List<QuestionBoard> getQuestionBoardList() {
        return questionBoardRepository.findByIsDeletedIsFalse();
    }

    public QuestionBoard insertQuestionBoard(QuestionBoard questionBoard) {
        return questionBoardRepository.save(questionBoard);
    }
}
