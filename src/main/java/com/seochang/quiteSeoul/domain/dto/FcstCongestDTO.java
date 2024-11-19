package com.seochang.quiteSeoul.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcstCongestDTO {

    private String fcstTime;
    private int fcstMax;
    private int fcstMin;
    private String fcstCongestLvl;
}
