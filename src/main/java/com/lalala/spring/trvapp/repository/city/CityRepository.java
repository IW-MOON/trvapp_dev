package com.lalala.spring.trvapp.repository.city;

import com.lalala.spring.trvapp.entity.city.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByCityNameStartsWith(String cityName);

    Optional<City> findAllByCityIdx(Long idx);
}
