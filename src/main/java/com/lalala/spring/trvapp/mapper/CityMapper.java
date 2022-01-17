package com.lalala.spring.trvapp.mapper;

import com.lalala.spring.trvapp.dto.board.TipBoardResponse;
import com.lalala.spring.trvapp.dto.city.CityResponse;
import com.lalala.spring.trvapp.entity.board.TipBoard;
import com.lalala.spring.trvapp.entity.city.City;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CityMapper {

    CityMapper INSTANCE = Mappers.getMapper(CityMapper.class);

    CityResponse cityToDto(City city);
}
