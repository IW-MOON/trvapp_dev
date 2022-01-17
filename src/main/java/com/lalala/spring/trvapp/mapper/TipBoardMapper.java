package com.lalala.spring.trvapp.mapper;

import com.lalala.spring.trvapp.dto.board.TipBoardResponse;
import com.lalala.spring.trvapp.entity.board.TipBoard;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TipBoardMapper {

    TipBoardMapper INSTANCE = Mappers.getMapper(TipBoardMapper.class);

    TipBoardResponse tipBoardToDto(TipBoard tipBoard);
}
