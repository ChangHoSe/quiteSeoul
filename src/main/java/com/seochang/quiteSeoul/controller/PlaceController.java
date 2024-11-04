package com.seochang.quiteSeoul.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seochang.quiteSeoul.data.PlaceDataService;
import com.seochang.quiteSeoul.domain.dto.PlaceWeatherDTO;
import com.seochang.quiteSeoul.service.PlaceService;
import com.seochang.quiteSeoul.data.InitialDataService;
import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final PlaceDataService placeDataService;

    @GetMapping("/")
    public String initialPlace(Model model) {
        List<String> placeNames =  placeService.findTop10Places();
        model.addAttribute("placeNames", placeNames);

        return "index";
    }

//    @GetMapping("/place/{placeName}")
//    public String detailPlace(@PathVariable String placeName, Model model) {
//        model.addAttribute("placeName", placeName);
//        placeService.getWeatherInfoByRegion(placeName)
//                .ifPresent(placeWeatherDTO -> {
//                    model.addAttribute("placeWeatherDTO", placeWeatherDTO);
//                });
//        placeService.getCongestionInfoByRegion(placeName)
//                .ifPresent(placeCongestionDTO -> {
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    String congestionDataJson = "";
//
//                    try {
//                        congestionDataJson = objectMapper.writeValueAsString(placeCongestionDTO.getFcstCongestDTO());
//                    } catch (JsonProcessingException e) {
//                        e.printStackTrace(); // 예외를 로그에 출력
//                    }
//
//                    model.addAttribute("placeCongestionDTO", placeCongestionDTO);
//                    model.addAttribute("congestionDataJson", congestionDataJson);
//                });
//        placeService.getEventInfoByRegion(placeName)
//                .ifPresent(placeEventListDTO -> {
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    String eventDataJson = "";
//
//                    try {
//                        eventDataJson = objectMapper.writeValueAsString(placeEventListDTO.getPlaceEventDTOList());
//                    } catch (JsonProcessingException e) {
//                        e.printStackTrace();
//                    }
//                    model.addAttribute("eventDataJson", eventDataJson);
//                });
//
//        return "detail";
//    }

    @GetMapping("/place/{placeName}")
    public String detailPlace(@PathVariable String placeName, Model model) {
        model.addAttribute("placeName", placeName);
        placeService.getPlaceInfo(placeName)
                .ifPresent(placeInfoDTO -> {
                    model.addAttribute("placeWeatherDTO", placeInfoDTO.getPlaceWeatherDTO());
                    model.addAttribute("placeCongestionDTO", placeInfoDTO.getPlaceCongestionDTO());

                    ObjectMapper objectMapper = new ObjectMapper();
                    String congestionDataJson = "";
                    String eventDataJson = "";
                    try {
                        congestionDataJson = objectMapper.writeValueAsString(placeInfoDTO.getPlaceCongestionDTO().getFcstCongestDTO());
                        eventDataJson = objectMapper.writeValueAsString(placeInfoDTO.getPlaceEventListDTO().getPlaceEventDTOList());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace(); // 예외를 로그에 출력
                    }
                    model.addAttribute("congestionDataJson", congestionDataJson);
                    model.addAttribute("eventDataJson", eventDataJson);

                });

        return "detail";
    }

    @GetMapping("/test-click")
    public String testClick () {
        placeDataService.processAllPlace();
        // 테스트 후 메인 페이지로 리디렉션
        return "redirect:/";
    }
}
