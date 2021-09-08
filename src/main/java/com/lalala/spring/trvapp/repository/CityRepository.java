package com.lalala.spring.trvapp.repository;

import com.lalala.spring.trvapp.entity.Auth;
import com.lalala.spring.trvapp.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {

    public List<City> findByCityNameStartsWith(String cityName);
}
