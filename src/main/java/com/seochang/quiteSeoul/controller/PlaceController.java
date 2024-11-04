package com.seochang.quiteSeoul.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final InitialDataService initialDataService;

    @GetMapping("/")
    public String initialPlace(Model model) {
        List<String> placeNames =  placeService.initialPlace();
        model.addAttribute("placeNames", placeNames);

        return "index";
    }

    @GetMapping("/place/{placeName}")
    public String detailPlace(@PathVariable String placeName, Model model) {
        model.addAttribute("placeName", placeName);
        placeService.getWeatherInfoByRegion(placeName)
                .ifPresent(placeWeatherDTO -> {
                    model.addAttribute("placeWeatherDTO", placeWeatherDTO);
                });
        placeService.getCongestionInfoByRegion(placeName)
                .ifPresent(placeCongestionDTO -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String congestionDataJson = "";

                    try {
                        congestionDataJson = objectMapper.writeValueAsString(placeCongestionDTO.getFcstCongestDTO());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace(); // 예외를 로그에 출력
                        // 필요에 따라 적절한 오류 처리를 수행
                    }

                    model.addAttribute("placeCongestionDTO", placeCongestionDTO);
                    model.addAttribute("congestionDataJson", congestionDataJson);
                });
        return "detail";
    }

    @GetMapping("/test-click")
    public String testClick () {

        // 테스트 후 메인 페이지로 리디렉션
        return "redirect:/";
    }
}
