package com.lalala.spring.trvapp.controller.board;

import com.lalala.spring.trvapp.dto.board.QuestionBoardRequest;
import com.lalala.spring.trvapp.dto.board.QuestionBoardResponse;
import com.lalala.spring.trvapp.dto.board.TipBoardRequest;
import com.lalala.spring.trvapp.dto.board.TipBoardResponse;
import com.lalala.spring.trvapp.entity.board.QuestionBoard;
import com.lalala.spring.trvapp.entity.board.TipBoard;
import com.lalala.spring.trvapp.entity.city.City;
import com.lalala.spring.trvapp.entity.user.User;
import com.lalala.spring.trvapp.interceptor.AuthenticationPrincipal;
import com.lalala.spring.trvapp.mapper.QuestionBoardMapper;
import com.lalala.spring.trvapp.mapper.TipBoardMapper;
import com.lalala.spring.trvapp.service.board.BoardService;
import com.lalala.spring.trvapp.service.city.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "board")
public class BoardController {

    private final BoardService boardService;
    private final CityService cityService;

    @GetMapping( value = "/tip")
    public ResponseEntity<List<TipBoardResponse>> getTipBoards() {

        List<TipBoardResponse> tipBoardResponses = boardService.getTipBoardList().stream()
                                                                .map(TipBoardMapper.INSTANCE::tipBoardToDto)
                                                                .collect(Collectors.toList());
        return new ResponseEntity<>(tipBoardResponses, HttpStatus.OK);
    }

    @PostMapping( value = "/tip")
    public ResponseEntity<TipBoardResponse> insertTipBoard(@RequestBody @Valid TipBoardRequest tipBoardRequest, @AuthenticationPrincipal User user) {

        City city = cityService.getCityByIdx(tipBoardRequest.getCityIdx());
        TipBoard tipBoard = boardService.insertTipBoard(TipBoard.of(tipBoardRequest, user, city));
        TipBoardResponse tipBoardResponse = TipBoardMapper.INSTANCE.tipBoardToDto(tipBoard);
        return new ResponseEntity<>(tipBoardResponse, HttpStatus.OK);
    }

    @GetMapping( value = "/question")
    public ResponseEntity<List<QuestionBoardResponse>> getQuestionBoards() {

        List<QuestionBoardResponse> questionBoardResponses = boardService.getQuestionBoardList().stream()
                .map(QuestionBoardMapper.INSTANCE::questionBoardToDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(questionBoardResponses, HttpStatus.OK);
    }

    @PostMapping( value = "/question")
    public ResponseEntity<QuestionBoardResponse> insertQuestionBoard(@RequestBody @Valid QuestionBoardRequest questionBoardRequest, @AuthenticationPrincipal User user) {

        City city = cityService.getCityByIdx(questionBoardRequest.getCityIdx());
        QuestionBoard questionBoard = boardService.insertQuestionBoard(QuestionBoard.of(questionBoardRequest, user, city));
        QuestionBoardResponse questionBoardResponse = QuestionBoardMapper.INSTANCE.questionBoardToDto(questionBoard);
        return new ResponseEntity<>(questionBoardResponse, HttpStatus.OK);
    }
}
