package com.seochang.quiteSeoul.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceEventDTO {

    private String url;
    private String isFree;
    private String eventName;
    private String thumbnail;
    private String eventPlace;
    private String eventPeriod;

}
