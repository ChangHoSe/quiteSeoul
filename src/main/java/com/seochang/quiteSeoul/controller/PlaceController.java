package com.seochang.quiteSeoul.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PlaceController {

    @GetMapping("/login")
    public String initial() {
        return "login";
    }
}
