package com.lalala.spring.trvapp.entity.city;

import com.lalala.spring.trvapp.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "CITY")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CITY_IDX",  nullable = false)
    private Long cityIdx;

    @Column(name = "CITY_NAME", nullable = false)
    private String cityName;

    @Column(name = "COUNTRY_NAME", nullable = false)
    private String countryName;

    public City(Long cityIdx) {
        this.cityIdx = cityIdx;
    }

    @Override
    public String toString() {
        return "City{" +
                "cityIdx=" + cityIdx +
                ", cityName='" + cityName + '\'' +
                ", countryName='" + countryName + '\'' +
                '}';
    }
}
