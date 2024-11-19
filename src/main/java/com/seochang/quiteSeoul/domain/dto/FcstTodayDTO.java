package com.seochang.quiteSeoul.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcstTodayDTO {
    //TODO : 안쓰는 수치는 String 으로 변환 예정
    private int temp;
    private String fcstDt;
    private String skyStts;
    private String precptType;
    private int rainChance;
    private String precipitation;
}
