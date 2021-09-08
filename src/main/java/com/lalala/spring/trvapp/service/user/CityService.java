package com.lalala.spring.trvapp.service.user;

import com.lalala.spring.trvapp.entity.City;
import com.lalala.spring.trvapp.exception.BadRequestException;
import com.lalala.spring.trvapp.model.CityResponse;
import com.lalala.spring.trvapp.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public ResponseEntity<List<CityResponse>> getCity(String cityName){

        if(cityName == null){
            throw new BadRequestException();
        }
        ModelMapper modelMapper = new ModelMapper();

        List<City> cityList = cityRepository.findByCityNameStartsWith(cityName);
        List<CityResponse> cityResponseList = cityList.stream().map(city -> modelMapper.map(city, CityResponse.class)).collect(Collectors.toList());

        return new ResponseEntity<List<CityResponse>>(cityResponseList, HttpStatus.OK);
    }

}
