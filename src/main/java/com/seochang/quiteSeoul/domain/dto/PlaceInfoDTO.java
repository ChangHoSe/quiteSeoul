package com.seochang.quiteSeoul.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaceInfoDTO {
    private PlaceWeatherDTO placeWeatherDTO;
    private PlaceEventListDTO placeEventListDTO;
    private PlaceCongestionDTO placeCongestionDTO;
}
