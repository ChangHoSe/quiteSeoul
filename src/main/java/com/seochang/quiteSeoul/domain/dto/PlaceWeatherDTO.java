package com.seochang.quiteSeoul.domain.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlaceWeatherDTO {

    private int pm10;
    private int pm25;

    private double temperature;
    private double maxTemp;
    private double minTemp;
    private double sensibleTemp;

    private String airIdx;
    private int humidity;
    private List<FcstTodayDTO> fcstTodayDTOList;

    @Override
    public String toString() {
        return "PlaceWeatherDTO{" +
               "pm10=" + pm10 +
               ", pm25=" + pm25 +
               ", temperature=" + temperature +
               ", maxTemp=" + maxTemp +
               ", minTemp=" + minTemp +
               ", sensibleTemp=" + sensibleTemp +
               ", airIdx='" + airIdx + '\'' +
               ", humidity=" + humidity +
               ", fcstTodayDTOList=" + fcstTodayDTOList +
               '}';
    }
}
