package com.lalala.spring.trvapp.controller.city;


import com.lalala.spring.trvapp.dto.CityResponse;
import com.lalala.spring.trvapp.service.city.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping(value = "city")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping(value = "/{cityName}" )
    public ResponseEntity<List<CityResponse>> getCity(@PathVariable(name = "cityName") String cityName)
    {
        return cityService.getCity(cityName);
    }

}
