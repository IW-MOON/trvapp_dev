package com.lalala.spring.trvapp.service.city;

import com.lalala.spring.trvapp.entity.city.City;
import com.lalala.spring.trvapp.dto.city.CityResponse;
import com.lalala.spring.trvapp.mapper.CityMapper;
import com.lalala.spring.trvapp.repository.city.CityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public List<CityResponse> getCityByCityName(String cityName){

        if(cityName == null){
            throw new IllegalArgumentException("Empty CityName!!");
        }
        List<City> cityList = cityRepository.findByCityNameStartsWith(cityName);

        return cityList.stream()
                    .map(CityMapper.INSTANCE::cityToDto)
                    .collect(Collectors.toList());
    }

    public City getCityByIdx(Long idx) {
        return cityRepository.findAllByCityIdx(idx)
                .orElseThrow(() -> new IllegalArgumentException("해당 도시가 존재하지 않습니다."));
    }

}
