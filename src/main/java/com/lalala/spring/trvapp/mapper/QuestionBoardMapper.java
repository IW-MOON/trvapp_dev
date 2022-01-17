package com.lalala.spring.trvapp.mapper;

import com.lalala.spring.trvapp.dto.board.QuestionBoardResponse;
import com.lalala.spring.trvapp.entity.board.QuestionBoard;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QuestionBoardMapper {

    QuestionBoardMapper INSTANCE = Mappers.getMapper(QuestionBoardMapper.class);

    QuestionBoardResponse questionBoardToDto(QuestionBoard questionBoard);
}
