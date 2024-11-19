package com.seochang.quiteSeoul.domain.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlaceCongestionDTO {

    private String areaCongestLvl;
    private String areaCongestMsg;

    private List<FcstCongestDTO> fcstCongestDTO;
}
