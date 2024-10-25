package com.seochang.quiteSeoul.controller;

import com.seochang.quiteSeoul.service.PlaceService;
import com.seochang.quiteSeoul.data.InitialDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final InitialDataService initialDataService;


    @GetMapping("/test-click")
    public String testClick () {

        // 테스트 후 메인 페이지로 리디렉션
        return "redirect:/";
    }
}
