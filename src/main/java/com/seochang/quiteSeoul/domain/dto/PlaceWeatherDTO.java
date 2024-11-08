package com.seochang.quiteSeoul.domain.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlaceWeatherDTO {

    private int pm10;
    private int pm25;

    private String pm10Index;
    private String pm25Index;

    private double temperature;
    private double maxTemp;
    private double minTemp;
    private double sensibleTemp;

    private String airIdx;
    private int humidity;
    private List<FcstTodayDTO> fcstTodayDTOList;
}
