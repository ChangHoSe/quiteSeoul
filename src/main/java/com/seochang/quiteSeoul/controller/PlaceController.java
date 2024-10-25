package com.seochang.quiteSeoul.controller;

import com.seochang.quiteSeoul.service.PlaceService;
import com.seochang.quiteSeoul.data.InitialDataService;
import java.util.List;
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
        return "detail";
    }

    @GetMapping("/test-click")
    public String testClick () {

        // 테스트 후 메인 페이지로 리디렉션
        return "redirect:/";
    }
}
